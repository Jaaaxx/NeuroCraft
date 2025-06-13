package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import static com.dementia.neurocraft.client.internal.PlayerSanityClientHandler.playerSanity;

public class CUpdatePlayerSanityPacket {
    private final int sanity;
    public CUpdatePlayerSanityPacket(int sanity) {
        this.sanity = sanity;
    }

    public CUpdatePlayerSanityPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(sanity);
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