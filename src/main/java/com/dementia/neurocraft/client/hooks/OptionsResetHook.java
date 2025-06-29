package com.dementia.neurocraft.client.hooks;

import com.dementia.neurocraft.client.internal.OptionsUtils;
import com.dementia.neurocraft.client.internal.ClientPlayerDeathEvent;
import com.dementia.neurocraft.client.internal.PlayerSanityClientHandler;
import com.dementia.neurocraft.config.ServerConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class OptionsResetHook {

    @SubscribeEvent
    public static void onLogin(PlayerEvent.PlayerLoggedInEvent e) {
        // Request initial sanity sync when player joins
        PlayerSanityClientHandler.onPlayerJoinWorld();
    }

    @SubscribeEvent
    public static void onLogout(PlayerEvent.PlayerLoggedOutEvent e) {
        OptionsUtils.reset();
        // Clear sanity data when player leaves
        PlayerSanityClientHandler.onPlayerLeaveWorld();
    }

    @SubscribeEvent
    public static void onDeath(ClientPlayerDeathEvent e) {
        OptionsUtils.reset();
        // Request sanity update after death in case it changed
        PlayerSanityClientHandler.updatePlayerSanityClient();
    }
}
