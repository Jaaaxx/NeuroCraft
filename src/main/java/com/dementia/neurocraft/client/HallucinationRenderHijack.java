package com.dementia.neurocraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.WalkAnimationState;
import net.minecraft.world.entity.player.Player;

import static com.dementia.neurocraft.Neurocraft.LOGGER;

public final class HallucinationRenderHijack {

    public static boolean done;
    public static PlayerRenderer PLAYER_RENDERER;

    public static void apply() {
        if (done) return;
        done = true;

        ByteBuddyAgent.install();

        new ByteBuddy()
                .redefine(LivingEntityRenderer.class)
                .visit(Advice.to(RenderAdvice.class).on(
                        ElementMatchers.isPublic()
                                .and(ElementMatchers.returns(void.class))
                                .and(ElementMatchers.takesArguments(6))
                                .and(ElementMatchers.takesArgument(0, LivingEntity.class))
                                .and(ElementMatchers.takesArgument(3, PoseStack.class))
                ))
                .make()
                .load(LivingEntityRenderer.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());


        LOGGER.info("Hallucination render hijack installed.");
    }

    public static final class DummyPlayer extends AbstractClientPlayer {
        public DummyPlayer(AbstractClientPlayer template) {
            super((ClientLevel) template.level(), template.getGameProfile());
        }

        @Override
        public boolean isSpectator() {
            return false;
        }

        @Override
        public boolean isCreative() {
            return false;
        }
    }

    public static final class RenderAdvice {
        public static com.mojang.authlib.GameProfile newRandomProfile(LivingEntity mob) {
            Minecraft mc = Minecraft.getInstance();
            java.util.UUID id = java.util.UUID.randomUUID();
            var profile = new com.mojang.authlib.GameProfile(id, randomName());
            mc.getSkinManager().getOrLoad(profile);
            return profile;
        }

        public static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);
        public static PlayerRenderer PLAYER_RENDERER;

        /* mob → profile cache (auto-removes when mob GC’d) */
        public static final java.util.Map<LivingEntity, com.mojang.authlib.GameProfile> PROFILES =
                new java.util.WeakHashMap<>();

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        public static boolean enter(@Advice.This Object renderer,
                                    @Advice.Argument(0) LivingEntity mob,
                                    @Advice.Argument(1) float yaw,
                                    @Advice.Argument(2) float pt,
                                    @Advice.Argument(3) PoseStack pose,
                                    @Advice.Argument(4) MultiBufferSource buf,
                                    @Advice.Argument(5) int light) {

            if (!RandomizeTextures.crazyRenderingActive) return false;
            if (renderer instanceof PlayerRenderer) return false;
            if (REENTRY.get()) return false;

            Minecraft mc = Minecraft.getInstance();
            var real = mc.player;
            if (mob == real) return false;

            /* lazily grab player renderer */
            if (PLAYER_RENDERER == null)
                PLAYER_RENDERER = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(real);

            /* obtain or create random profile for this mob */
            com.mojang.authlib.GameProfile prof =
                    PROFILES.computeIfAbsent(mob, RenderAdvice::newRandomProfile);

            /* build disposable dummy with that profile */
            DummyPlayer dummy = new DummyPlayer(mob.level(), prof);

            mirrorState(mob, dummy);

            REENTRY.set(true);
            try {
                PLAYER_RENDERER.render(dummy, yaw, pt, pose, buf, light);
            } finally {
                REENTRY.set(false);
            }

            return true;
        }

        /* ---------- helpers ---------- */

        public static void mirrorState(LivingEntity src,
                                       DummyPlayer dst) {

            /* ─ position / rotation ─ */
            dst.setPos(src.getX(), src.getY(), src.getZ());
            dst.setYRot(src.getYRot());
            dst.setXRot(src.getXRot());
            dst.yHeadRot = src.yHeadRot;
            dst.yHeadRotO = src.yHeadRotO;
            dst.yBodyRot = src.yBodyRot;
            dst.yBodyRotO = src.yBodyRotO;

            WalkAnimationState srcWalk = src.walkAnimation;
            WalkAnimationState dstWalk = dst.walkAnimation;

            double dx = src.getX() - src.xo;
            double dz = src.getZ() - src.zo;
            float target = Math.min((float) Math.sqrt(dx * dx + dz * dz) * 4.0F, 1.0F);

            dstWalk.update(target, 0.4F);

            dst.attackAnim = src.attackAnim;
            dst.hurtTime = src.hurtTime;
            dst.setPose(src.getPose());
        }


        /**
         * simple pronounceable 6-10 char string
         */
        public static String randomName() {
            String[] syll = {"ba", "be", "bi", "bo", "bu", "ka", "ke", "ki", "ko", "ku", "ra", "re", "ri", "ro", "ru",
                    "ta", "te", "ti", "to", "tu", "za", "ze", "zi", "zo", "zu"};
            java.util.Random r = java.util.concurrent.ThreadLocalRandom.current();
            int len = 3 + r.nextInt(3);            // 3–5 syllables
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < len; i++) sb.append(syll[r.nextInt(syll.length)]);
            return sb.substring(0, Math.min(16, sb.length()));
        }

        /* Disposable client-side player */
        public static final class DummyPlayer extends AbstractClientPlayer {
            public DummyPlayer(net.minecraft.world.level.Level lvl,
                               com.mojang.authlib.GameProfile gp) {
                super((ClientLevel) lvl, gp);
            }

            @Override
            public boolean isSpectator() {
                return false;
            }

            @Override
            public boolean isCreative() {
                return false;
            }
        }
    }
}
