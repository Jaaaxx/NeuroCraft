package com.dementia.neurocraft.client;

import com.mojang.authlib.GameProfile;
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

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Field;

import static com.dementia.neurocraft.Neurocraft.LOGGER;
import static com.dementia.neurocraft.client.PlayerHallucinations.getRandomName;

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
        public DummyPlayer(net.minecraft.world.level.Level lvl, GameProfile gp) {
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
    /* ---------- WalkAnimationState reflective helpers ---------- */
    public static final class WalkAnimMirror {

        public static final Field POS, SPD, SPD_OLD;

        static {
            try {
                POS     = WalkAnimationState.class.getDeclaredField("position");
                SPD     = WalkAnimationState.class.getDeclaredField("speed");
                SPD_OLD = WalkAnimationState.class.getDeclaredField("speedOld");
                AccessibleObject.setAccessible(
                        new AccessibleObject[]{POS, SPD, SPD_OLD}, true);
            } catch (ReflectiveOperationException e) {
                throw new RuntimeException("Cannot access WalkAnimationState internals", e);
            }
        }

        public static void copy(WalkAnimationState from, WalkAnimationState to) {
            try {
                POS.setFloat(to,     POS.getFloat(from));
                SPD.setFloat(to,     SPD.getFloat(from));
                SPD_OLD.setFloat(to, SPD_OLD.getFloat(from));
            } catch (IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        }

        public static float getSpeed(WalkAnimationState s) {
            try {
                return SPD.getFloat(s);
            } catch (IllegalAccessException ex) {
                throw new AssertionError(ex);
            }
        }
    }


    public static final class RenderAdvice {
        public static DummyPlayer getOrCreateDummy(LivingEntity mob) {
            return DUMMIES.computeIfAbsent(mob, m -> {
                GameProfile profile = PROFILES.computeIfAbsent(m, RenderAdvice::newRandomProfile);
                return new DummyPlayer(m.level(), profile);
            });
        }

        /* mob → cached dummy (GC-safe) */
        public static final java.util.Map<LivingEntity, DummyPlayer> DUMMIES =
                new java.util.WeakHashMap<>();
        public static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);
        /* mob → profile cache (auto-removes when mob GC’d) */
        public static final java.util.Map<LivingEntity, com.mojang.authlib.GameProfile> PROFILES =
                new java.util.WeakHashMap<>();
        public static com.mojang.authlib.GameProfile newRandomProfile(LivingEntity mob) {
            Minecraft mc = Minecraft.getInstance();
            java.util.UUID id = java.util.UUID.randomUUID();
            var profile = new com.mojang.authlib.GameProfile(id, getRandomName());
            mc.getSkinManager().getOrLoad(profile);
            return profile;
        }
        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        public static boolean enter(@Advice.Argument(0) LivingEntity mob,
                                    @Advice.Argument(1) float yaw,
                                    @Advice.Argument(2) float pt,
                                    @Advice.Argument(3) PoseStack pose,
                                    @Advice.Argument(4) MultiBufferSource buf,
                                    @Advice.Argument(5) int light) {

            if (!RandomizeTextures.crazyRenderingActive
                    || mob == Minecraft.getInstance().player
                    || REENTRY.get())
                return false;

            /* renderer & profile unchanged … */

            /* one dummy per mob — keeps animation history */
            DummyPlayer dummy = getOrCreateDummy(mob);

            mirrorState(mob, dummy, pt);

            REENTRY.set(true);
            try {
                if (PLAYER_RENDERER == null) {
                    var real = Minecraft.getInstance().player;
                    PLAYER_RENDERER = (PlayerRenderer)
                            Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(real);
                }
                PLAYER_RENDERER.render(dummy, yaw, pt, pose, buf, light);
            } finally {
                REENTRY.set(false);
            }
            return true;
        }

        /* ---------- helpers ---------- */

        public static void mirrorState(LivingEntity src, DummyPlayer dst, float pt) {
            // Position & old position
            dst.setPos(src.getX(), src.getY(), src.getZ());
            dst.xo = src.xo;
            dst.yo = src.yo;
            dst.zo = src.zo;

            // Rotations
            dst.setYRot(src.getYRot());
            dst.setXRot(src.getXRot());
            dst.yHeadRot  = src.yHeadRot;
            dst.yHeadRotO = src.yHeadRotO;
            dst.yBodyRot  = src.yBodyRot;
            dst.yBodyRotO = src.yBodyRotO;

            // Recompute walk speed based on movement
            // --- Walk-cycle synchronisation ---
            if (dst.tickCount != src.tickCount) {               // run exactly once per tick
                double dx = src.getX() - src.xo;
                double dz = src.getZ() - src.zo;
                float speed = Math.min((float) Math.sqrt(dx*dx + dz*dz) * 4.0F, 1.0F);
                dst.walkAnimation.update(speed, 1.0F);          // delta = 1 tick, not partial-tick
                dst.tickCount = src.tickCount;                  // keep in step
            }

            // Combat & pose
            dst.attackAnim = src.attackAnim;
            dst.hurtTime   = src.hurtTime;
            dst.setPose(src.getPose());
        }

    }
}
