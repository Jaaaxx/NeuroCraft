package com.dementia.neurocraft.client.internal;

import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public class PlayerSanityClientHandler {
    public static int playerSanity = 0;

    public static void updatePlayerSanityClient() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null)
            return;


        PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
    }

    public static int getPlayerSanityClient() {
        updatePlayerSanityClient();
        return playerSanity;
    }
}
