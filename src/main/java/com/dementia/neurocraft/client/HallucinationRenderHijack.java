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
import net.minecraft.world.entity.player.Player;

public final class HallucinationRenderHijack {

    public static boolean done;
    public static PlayerRenderer PLAYER_RENDERER;

    public static void apply() {
        if (done) return;
        done = true;

        ByteBuddyAgent.install();

        new ByteBuddy()
                .redefine(LivingEntityRenderer.class)
                .visit(Advice.to(RenderAdvice.class)
                        .on(ElementMatchers.named("render")))
                .make()
                .load(LivingEntityRenderer.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());

        System.out.println("Hallucination render hijack installed.");
    }
    public static final class DummyPlayer extends AbstractClientPlayer {
        public DummyPlayer(AbstractClientPlayer template) {
            super((ClientLevel) template.level(), template.getGameProfile());
        }
        @Override public boolean isSpectator() { return false; }
        @Override public boolean isCreative()  { return false; }
    }

    public static final class RenderAdvice {

        public static final ThreadLocal<Boolean> REENTRY = ThreadLocal.withInitial(() -> false);
        public static PlayerRenderer PLAYER_RENDERER;
        public static DummyPlayer   DUMMY;

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean enter(@Advice.This Object renderer,
                             @Advice.Argument(0) LivingEntity mob,
                             @Advice.Argument(1) float yaw,
                             @Advice.Argument(2) float pt,
                             @Advice.Argument(3) PoseStack pose,
                             @Advice.Argument(4) MultiBufferSource buf,
                             @Advice.Argument(5) int light) {

            /* ── vanilla path ── */
            if (!RandomizeTextures.crazyRenderingActive) return false;
            if (renderer instanceof PlayerRenderer)       return false;  // avoid hijacking the player renderer
            if (REENTRY.get())                            return false;  // recursion guard

            Minecraft mc = Minecraft.getInstance();
            var real = mc.player;
            if (mob == real) return false;                                // don’t duplicate the real player

            /* ── lazy init ── */
            if (PLAYER_RENDERER == null)
                PLAYER_RENDERER = (PlayerRenderer) mc.getEntityRenderDispatcher().getRenderer(real);

            if (DUMMY == null || DUMMY.level() != mob.level())
                DUMMY = new DummyPlayer(real);                            // one dummy per level

            /* ── mirror mob state into dummy ── */
            DUMMY.setPos(mob.getX(), mob.getY(), mob.getZ());
            DUMMY.setYRot(mob.getYRot());
            DUMMY.setXRot(mob.getXRot());
            DUMMY.yHeadRot   = mob.getYHeadRot();
            DUMMY.yHeadRotO  = mob.yHeadRotO;
            DUMMY.yBodyRot   = mob.yBodyRot;
            DUMMY.yBodyRotO  = mob.yBodyRotO;

            // basic movement animation
            DUMMY.walkAnimation.update(mob.walkAnimation.position(), mob.walkAnimation.speed());
            DUMMY.attackAnim = mob.attackAnim;
            DUMMY.hurtTime   = mob.hurtTime;
            DUMMY.setPose(mob.getPose());

            /* ── render with recursion guard ── */
            REENTRY.set(true);
            try {
                PLAYER_RENDERER.render(DUMMY, yaw, pt, pose, buf, light);
            } finally {
                REENTRY.set(false);
            }
            return true;   // skip original mob render
        }

        /* minimal dummy that shares the real player’s profile/skin */
        public static final class DummyPlayer extends AbstractClientPlayer {
            public DummyPlayer(AbstractClientPlayer template) {
                super((ClientLevel) template.level(), template.getGameProfile());
            }
            @Override public boolean isSpectator() { return false; }
            @Override public boolean isCreative()  { return false; }
        }
    }

}
