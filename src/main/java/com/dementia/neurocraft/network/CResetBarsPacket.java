package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.ClientCustomPayloadEvent;

import java.util.function.Supplier;

import static com.dementia.neurocraft.client.PlayerSanityClientHandler.playerSanity;
import static com.dementia.neurocraft.client.RandomizeHealthBars.resetBarsToServer;

public class CResetBarsPacket {
    private final int foodLevel;
    private final float healthLevel;

    public CResetBarsPacket(int foodLevel, float healthLevel) {
        this.foodLevel = foodLevel;
        this.healthLevel = healthLevel;
    }

    public CResetBarsPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt(), buffer.readFloat());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(foodLevel);
        buffer.writeFloat(healthLevel);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        resetBarsToServer(foodLevel, healthLevel);
    }
}