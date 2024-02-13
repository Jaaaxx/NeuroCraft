package com.dementia.neurocraft.server;

import com.dementia.neurocraft.Neurocraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashMap;
import java.util.Random;

import static com.dementia.neurocraft.config.ServerConfigs.RANDOM_TELEPORTING;
import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID)
public class RandomTeleporting {
    private static int c = 1;
    private static final HashMap<Player, Vec3> playerTeleportLocs = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (!RANDOM_TELEPORTING.get())
            return;

        if (event.phase == TickEvent.Phase.END) {
            if (c++ % (20 * 5) == 0) {
                for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
                    queuePlayerTeleport(p);
                }
            }
            if (c % (20 * 10) == 0) {
                for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
                    if (playerTeleportLocs.containsKey(p))
                        teleportPlayerBack(p);
                }
                c = 1;
            }
        }
    }

    private static void queuePlayerTeleport(Player p) {
        var playerSanity = getPlayerSanity(p);
        boolean queueTP = (new Random().nextInt((int) (PEAK_SANITY.get()*1.25)) < playerSanity);
        if (queueTP) {
            playerTeleportLocs.put(p, p.getEyePosition());
        }
    }
    private static void teleportPlayerBack(Player p) {
        p.setPos(playerTeleportLocs.get(p));
        playerTeleportLocs.remove(p);
        HallucinationOccured(p);
    }
}
