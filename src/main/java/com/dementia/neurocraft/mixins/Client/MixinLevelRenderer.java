package com.dementia.neurocraft.mixins.Client;

import com.dementia.neurocraft.client.RandomizeTextures;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.RenderType;
import org.joml.Matrix4f;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LevelRenderer.class)
public abstract class MixinLevelRenderer {
    @Inject(at = @At("HEAD"), method = "renderSectionLayer(Lnet/minecraft/client/renderer/RenderType;Lcom/mojang/blaze3d/vertex/PoseStack;DDDLorg/joml/Matrix4f;)V", cancellable = true)
    private void cancelRenderSectionLayer(RenderType type, PoseStack poseStack, double camX, double camY, double camZ, Matrix4f projectionMatrix, CallbackInfo ci) {
        if (RandomizeTextures.crazyRenderingActive) {
            ci.cancel();
        }
    }

    /*  renderSky(PoseStack, Matrix4f, float, Camera, boolean, Runnable) */
    @Inject(method = "renderSky(Lcom/mojang/blaze3d/vertex/PoseStack;" + "Lorg/joml/Matrix4f;F" + "Lnet/minecraft/client/Camera;Z" + "Ljava/lang/Runnable;)V", at = @At("HEAD"), cancellable = true)
    private void neurocraft$skipSky(CallbackInfo ci) {
        if (com.dementia.neurocraft.client.RandomizeTextures.crazyRenderingActive) {
            ci.cancel();
        }
    }

    /*  renderClouds(PoseStack, Matrix4f, float, double, double, double) */
    @Inject(method = "renderClouds(Lcom/mojang/blaze3d/vertex/PoseStack;" + "Lorg/joml/Matrix4f;FDDD)V", at = @At("HEAD"), cancellable = true)
    private void neurocraft$skipClouds(CallbackInfo ci) {
        if (com.dementia.neurocraft.client.RandomizeTextures.crazyRenderingActive) {
            ci.cancel();
        }
    }
}