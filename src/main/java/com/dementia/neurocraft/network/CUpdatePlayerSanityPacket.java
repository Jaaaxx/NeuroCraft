package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.internal.PlayerSanityClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

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
            // Use the new method that properly tracks per-player sanity
            PlayerSanityClientHandler.setPlayerSanityClient(sanity);
            context.setPacketHandled(true);
        } else {
            context.setPacketHandled(false);
        }
    }
}