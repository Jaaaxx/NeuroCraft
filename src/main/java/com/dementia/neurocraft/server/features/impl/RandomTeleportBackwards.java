package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.HashMap;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;


public final class RandomTeleportBackwards extends Feature {

    private final HashMap<ServerPlayer, Vec3> playerTeleportLocs = new HashMap<>();

    public RandomTeleportBackwards() {
        super("RANDOM_TELEPORTING", "Random Teleporting", 100, 0.4, 8, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (!playerTeleportLocs.containsKey(player)) {
            playerTeleportLocs.put(player, player.position());
        } else {
            Vec3 eye = playerTeleportLocs.get(player);
            player.connection.teleport(eye.x, eye.y, eye.z, player.getYRot(), player.getXRot());
            playerTeleportLocs.remove(player);
            HallucinationOccured(player, false, true);
        }
    }
}
