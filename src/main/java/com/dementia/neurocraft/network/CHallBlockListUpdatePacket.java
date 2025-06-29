package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.internal.HallBlockClientHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class CHallBlockListUpdatePacket {
    private final int[] coords;

    public CHallBlockListUpdatePacket(int[] coords) {
        this.coords = coords;
    }

    public CHallBlockListUpdatePacket(FriendlyByteBuf buf) {
        int length = buf.readInt();
        this.coords = new int[length];
        for (int i = 0; i < length; i++) {
            this.coords[i] = buf.readInt();
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(coords.length);
        for (int coord : coords) {
            buf.writeInt(coord);
        }
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        if (!ctx.isClientSide()) {
            ctx.setPacketHandled(false);
            return;
        }

        ctx.enqueueWork(() -> {
            if (coords.length >= 3) {
                BlockPos pos = new BlockPos(coords[0], coords[1], coords[2]);
                HallBlockClientHandler.updateHallucinations(pos);
            }
        });
        
        ctx.setPacketHandled(true);
    }
} 