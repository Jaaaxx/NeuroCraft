package com.dementia.neurocraft.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.agent.ByteBuddyAgent;
import net.bytebuddy.asm.Advice;
import net.bytebuddy.dynamic.loading.ClassReloadingStrategy;
import net.bytebuddy.matcher.ElementMatchers;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;

/**  call once after the first client world is present  */
public final class HallucinationRenderHijack {

    public static boolean done;
    public static PlayerRenderer PLAYER_RENDERER;          // shared classic-arm player model

    public static void apply() {
        if (done) return;
        done = true;

        ByteBuddyAgent.install();

        /* inject into the base class that *every* mob render call goes through */
        new ByteBuddy()
                .redefine(LivingEntityRenderer.class)
                .visit(Advice.to(RenderAdvice.class)
                        .on(ElementMatchers.named("render")))
                .make()
                .load(LivingEntityRenderer.class.getClassLoader(),
                        ClassReloadingStrategy.fromInstalledAgent());

        System.out.println("Hallucination render hijack installed.");
    }

    /* ───────────────────────── advice ───────────────────────── */
    public static final class RenderAdvice {

        @Advice.OnMethodEnter(skipOn = Advice.OnNonDefaultValue.class)
        static boolean enter(@Advice.Argument(0) LivingEntity target,
                             @Advice.Argument(1) float yaw,
                             @Advice.Argument(2) float partialTicks,
                             @Advice.Argument(3) PoseStack pose,
                             @Advice.Argument(4) MultiBufferSource buffers,
                             @Advice.Argument(5) int packedLight) {

            if (!com.dementia.neurocraft.client.RandomizeTextures.crazyRenderingActive) return false;      // vanilla path

            Minecraft mc = Minecraft.getInstance();
            var player = mc.player;
            if (target == player) return false;                             // don’t double-draw the player

            if (PLAYER_RENDERER == null) {                                  // lazy-init once
                PLAYER_RENDERER = (PlayerRenderer) mc.getEntityRenderDispatcher()
                        .getRenderer(player);  // already baked by MC
            }

            PLAYER_RENDERER.render(player, yaw, partialTicks,
                    pose, buffers, packedLight);            // draw player model

            return true;   // ← skip the original mob render()
        }
    }
}
