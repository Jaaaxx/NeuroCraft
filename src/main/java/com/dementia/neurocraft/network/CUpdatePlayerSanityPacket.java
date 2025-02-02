package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.ClientCustomPayloadEvent;

import java.util.function.Supplier;

import static com.dementia.neurocraft.client.PlayerSanityClientHandler.playerSanity;

public class CUpdatePlayerSanityPacket {
    private final long sanity;

    public CUpdatePlayerSanityPacket(long sanity) {
        this.sanity = sanity;
    }

    public CUpdatePlayerSanityPacket(FriendlyByteBuf buffer) {
        this(buffer.readLong());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeLong(sanity);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        playerSanity = sanity;
    }
}