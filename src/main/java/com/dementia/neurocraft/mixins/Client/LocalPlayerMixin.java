package com.dementia.neurocraft.mixins.Client;

import com.dementia.neurocraft.client.ClientPlayerDeathEvent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LocalPlayer.class)
public abstract class LocalPlayerMixin {
    @Inject(method = "setShowDeathScreen", at = @At("HEAD"))
    private void onDeathScreenShown(boolean show, CallbackInfo ci) {
        if (show) {
            MinecraftForge.EVENT_BUS.post(new ClientPlayerDeathEvent());
        }
    }
}

