package com.dementia.neurocraft.client.mixins;

import com.dementia.neurocraft.client.internal.ClientPlayerDeathEvent;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.client.player.LocalPlayer.class)
public abstract class PsychosisLocalPlayer {
    @Inject(method = "setShowDeathScreen(Z)V", at = @At("HEAD"))
    private void onDeathScreenShown(boolean show, CallbackInfo ci) {
        if (show) {
            MinecraftForge.EVENT_BUS.post(new ClientPlayerDeathEvent());
        }
    }
}

