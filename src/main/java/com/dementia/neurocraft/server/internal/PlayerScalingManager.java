package com.dementia.neurocraft.server.internal;

import com.dementia.neurocraft.Neurocraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;
import static com.dementia.neurocraft.common.util.HallucinationUtils.SCALING_INCREMENT;
import static com.dementia.neurocraft.config.ServerConfigs.*;

@EventBusSubscriber(modid = Neurocraft.MODID)
public class PlayerScalingManager {
    static private int c = 0;

    public static void giveInitialSanity(final Player player) {
        player.getPersistentData().putInt("Sanity", INITIAL_SANITY.get());
    }

    public static int getPlayerSanity(final Player player) {
        if (!player.getPersistentData().contains("Sanity")) {
            giveInitialSanity(player);
        }
        return player.getPersistentData().getInt("Sanity");
    }

    public static void incrementPlayerSanity(final Player player) {
        int currentSanity = getPlayerSanity(player);
        if (currentSanity >= PEAK_SANITY)
            return;
        if (PLAYER_SCALING.get()) {
            player.getPersistentData().putInt("Sanity", currentSanity + SCALING_INCREMENT);
        }
    }

    public static void setPlayerSanity(final Player player, int newSanity) {
        int currentSanity = getPlayerSanity(player);
        player.getPersistentData().putInt("Sanity", newSanity);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.PLAYER && event.side == LogicalSide.SERVER) {
            if (c++ == 20 * SCALING_INTERVAL.get()) {
                incrementPlayerSanity(event.player);
                c = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        if (SANITY_RESET_UPON_DEATH.get()) {
            giveInitialSanity(event.getEntity());
        }
    }
}