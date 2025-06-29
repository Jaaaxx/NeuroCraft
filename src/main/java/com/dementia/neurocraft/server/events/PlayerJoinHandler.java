package com.dementia.neurocraft.server.events;

import com.dementia.neurocraft.config.ServerConfigs;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class PlayerJoinHandler {

    @SubscribeEvent
    public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            // Send initial config values to the joining player
            ServerConfigs.sendConfigValuesToPlayer(player);
        }
    }
} 