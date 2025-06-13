package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;


public final class RandomTeleportingHallucination extends Feature {

    private final HashMap<ServerPlayer, Vec3> playerTeleportLocs = new HashMap<>();

    public RandomTeleportingHallucination() {
        super("RANDOM_TELEPORTING", "Random Teleporting",
                600,        // sanity threshold
                0.4,        // max chance at PEAK_SANITY
                5,          // try every 5s
                true, FeatureTrigger.TICK);      // enabled by default
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (!playerTeleportLocs.containsKey(player)) {
            playerTeleportLocs.put(player, player.getEyePosition());
        } else {
            player.setPos(playerTeleportLocs.get(player));
            playerTeleportLocs.remove(player);
            HallucinationOccured(player, false, true);
        }
    }
}
