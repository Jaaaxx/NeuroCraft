package com.dementia.neurocraft.client.mixins;

import com.dementia.neurocraft.client.features.impl.Psychosis;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static com.dementia.neurocraft.client.features.impl.Psychosis.RENDER_RADIUS;

@Mixin(com.mojang.blaze3d.systems.RenderSystem.class)
public abstract class PsychosisRenderSystem {

    @Shadow
    private static float shaderFogStart;
    @Shadow
    private static float shaderFogEnd;
    @Final
    @Shadow
    private static float[] shaderFogColor;

    @Inject(method = "_setShaderFogStart(F)V", at = @At("TAIL"))
    private static void forceStart(float ignored, CallbackInfo ci) {
        if (Psychosis.active) shaderFogStart = -0.5F;
    }

    @Inject(method = "_setShaderFogEnd(F)V", at = @At("TAIL"))
    private static void forceEnd(float ignored, CallbackInfo ci) {
        if (Psychosis.active) shaderFogEnd = RENDER_RADIUS;
    }

    @Inject(method = "_setShaderFogColor(FFFF)V", at = @At("TAIL"))
    private static void recolour(float r, float g, float b, float a, CallbackInfo ci) {
        if (Psychosis.active) {
            shaderFogColor[0] = 0.0F;
            shaderFogColor[1] = 0.0F;
            shaderFogColor[2] = 0.0F;
        }
    }
}