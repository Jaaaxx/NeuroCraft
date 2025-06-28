package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import static com.dementia.neurocraft.client.internal.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;

public final class PlayerDisorientation extends Feature {
    public PlayerDisorientation() {
        super("PLAYER_DISORIENTATION", "Player Disorientation", 120, 1, 15, true, FeatureTrigger.TICK);
    }

    @Override
    public void performClient(Minecraft mc) {
        Player player = mc.player;
        if (player == null) return;

        // LOOK AT RANDOM DIRECTION
        Random rand = new Random();
        double theta = rand.nextDouble() * 2 * Math.PI;
        double phi = Math.acos(2 * rand.nextDouble() - 1);
        double x = Math.sin(phi) * Math.cos(theta);
        double y = Math.sin(phi) * Math.sin(theta);
        double z = Math.cos(phi);
        double distance = 10.0;
        Vec3 lookTarget = player.getEyePosition(1.0F).add(x * distance, y * distance, z * distance);
        player.lookAt(EntityAnchorArgument.Anchor.EYES, lookTarget);

        HallucinationOccuredClient();
    }
}
