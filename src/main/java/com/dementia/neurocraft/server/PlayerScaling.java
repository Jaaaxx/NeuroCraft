package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import com.mojang.logging.LogUtils;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.registries.ForgeRegistries;

@EventBusSubscriber(modid = NeuroCraft.MODID)
public class PlayerScaling {
    static private int c = 0;

    // 800 Takes about 2 hours to reach
    static public final int PEAK_SANITY = 10;

    public static void giveInitialSanity(final Player player) {
        player.getPersistentData().putLong("Sanity", 0);
    }

    public static long getPlayerSanity(final Player player) {
        return player.getPersistentData().getLong("Sanity");
    }

    public static void incrementPlayerSanity(final Player player) {
        long currentSanity = getPlayerSanity(player);
        if (currentSanity >= PEAK_SANITY)
            return;
        player.getPersistentData().putLong("Sanity", currentSanity + 1);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.PLAYER && event.side == LogicalSide.SERVER) {
            if (c++ == 20 * 30) {
                incrementPlayerSanity(event.player);
                LogUtils.getLogger().info("" + getPlayerSanity(event.player));
                c = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        giveInitialSanity(event.getEntity());
    }
}