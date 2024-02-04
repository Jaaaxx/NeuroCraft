package com.dementia.neurocraft.server;

import com.dementia.neurocraft.EnabledFeatures;
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

import java.util.Random;

@EventBusSubscriber(modid = NeuroCraft.MODID)
public class PlayerScaling {
    static private int c = 0;
    static public final int PEAK_SANITY = 800;
    static public final long INITIAL_SANITY = 800;
    static public final int SCALING_INTERVAL = 30; // seconds

    private static boolean sanityGiven = false;

    public static void giveInitialSanity(final Player player) {
        player.getPersistentData().putLong("Sanity", INITIAL_SANITY);
    }

    public static long getPlayerSanity(final Player player) {
        if (!sanityGiven) {
            sanityGiven = true;
            giveInitialSanity(player);
        }
        return player.getPersistentData().getLong("Sanity");
    }

    public static void incrementPlayerSanity(final Player player) {
        long currentSanity = getPlayerSanity(player);
        if (currentSanity >= PEAK_SANITY)
            return;
        if (EnabledFeatures.PLAYER_SCALING) {
            player.getPersistentData().putLong("Sanity", currentSanity + 1);
        }
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END && event.type == TickEvent.Type.PLAYER && event.side == LogicalSide.SERVER) {
            if (c++ == 20 * SCALING_INTERVAL) {
                incrementPlayerSanity(event.player);
                c = 0;
            }
        }
    }

    @SubscribeEvent
    public static void onPlayerSpawn(PlayerEvent.PlayerRespawnEvent event) {
        giveInitialSanity(event.getEntity());
    }
}