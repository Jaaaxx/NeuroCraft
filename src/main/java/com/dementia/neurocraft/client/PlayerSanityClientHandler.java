package com.dementia.neurocraft.client;

import com.dementia.neurocraft.network.CUpdatePlayerSanityPacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;

public class PlayerSanityClientHandler {
    public static long playerSanity = 0;

    public static void updatePlayerSanityClient() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;


        PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
    }

    public static long getPlayerSanityClient() {
        updatePlayerSanityClient();
        return playerSanity;
    }
}
