package com.dementia.neurocraft.client.mixins;

import com.dementia.neurocraft.client.features.impl.Psychosis;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.renderer.LevelRenderer.class)
public abstract class PsychosisLevelRenderer {
    @Inject(at = @At("HEAD"), method = "renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V", cancellable = true)
    private void cancelRenderSectionLayer(RenderType type, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (Psychosis.active) {
            ci.cancel();
        }
    }

    /*  renderSky(PoseStack, Matrix4f, float, Camera, boolean, Runnable) */
    @Inject(method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;" + "Lorg/joml/Matrix4f;F" + "Lnet/minecraft/client/Camera;Z" + "Ljava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void neurocraft$skipSky(CallbackInfo ci) {
        if (Psychosis.active) {
            ci.cancel();
        }
    }

    /*  renderClouds(PoseStack, Matrix4f, float, double, double, double) */
    @Inject(method = "renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;" + "Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    private void neurocraft$skipClouds(CallbackInfo ci) {
        if (Psychosis.active) {
            ci.cancel();
        }
    }
}