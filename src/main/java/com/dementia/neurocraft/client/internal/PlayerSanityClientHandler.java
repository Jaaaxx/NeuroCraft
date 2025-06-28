package com.dementia.neurocraft.client.internal;

import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerSanityClientHandler {
    // Store sanity per player UUID instead of a global static value
    private static final Map<UUID, Integer> playerSanityMap = new HashMap<>();
    private static int lastKnownSanity = 0;

    public static void updatePlayerSanityClient() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;

        PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
    }

    public static int getPlayerSanityClient() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) {
            return lastKnownSanity;
        }
        
        // Get sanity for the local player specifically
        Integer sanity = playerSanityMap.get(player.getUUID());
        if (sanity != null) {
            lastKnownSanity = sanity;
            return sanity;
        }
        
        // If no sanity data yet, request update and return last known
        updatePlayerSanityClient();
        return lastKnownSanity;
    }
    
    /**
     * Called by CUpdatePlayerSanityPacket to update the local player's sanity
     */
    public static void setPlayerSanityClient(int sanity) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null) {
            playerSanityMap.put(player.getUUID(), sanity);
            lastKnownSanity = sanity;
        }
    }
}
