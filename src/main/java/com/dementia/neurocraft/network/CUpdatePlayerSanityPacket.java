package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isClientSide()) {
            playerSanity = sanity;
            context.setPacketHandled(true);
        } else {
            context.setPacketHandled(false);
        }
    }
}