package com.dementia.neurocraft.mixins.Client;

import com.dementia.neurocraft.client.RandomizeTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.dementia.neurocraft.client.RandomizeTextures.RENDER_RADIUS;

@Mixin(RenderSystem.class)
public abstract class HeavyFogMixin {

    /* ---------- access to RenderSystem’s private static fields ---------- */

    @Shadow
    private static float shaderFogStart;
    @Shadow
    private static float shaderFogEnd;
    @Final
    @Shadow
    private static float[] shaderFogColor;

    /* ----------------------- distance parameters ------------------------ */

    @Inject(method = "_setShaderFogStart(F)V", at = @At("TAIL"))
    private static void forceStart(float ignored, CallbackInfo ci) {
        if (RandomizeTextures.crazyRenderingActive) shaderFogStart = -0.5F;
    }

    @Inject(method = "_setShaderFogEnd(F)V", at = @At("TAIL"))
    private static void forceEnd(float ignored, CallbackInfo ci) {
        if (RandomizeTextures.crazyRenderingActive) shaderFogEnd = RENDER_RADIUS;        // ≈1.5 blocks
    }

    /* --------------------------- colour tint ---------------------------- */

    @Inject(method = "_setShaderFogColor(FFFF)V", at = @At("TAIL"))
    private static void recolour(float r, float g, float b, float a, CallbackInfo ci) {
        if (RandomizeTextures.crazyRenderingActive) {
            shaderFogColor[0] = 0.0F;
            shaderFogColor[1] = 0.0F;
            shaderFogColor[2] = 0.0F;
            /* alpha unchanged – shaderFogColor[3] */
        }
    }
}