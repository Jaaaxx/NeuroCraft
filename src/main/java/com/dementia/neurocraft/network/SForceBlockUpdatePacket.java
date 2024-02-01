package com.dementia.neurocraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SForceBlockUpdatePacket {
    private final int[] blockPos;

    public SForceBlockUpdatePacket(int[] bufferPos) {
        this.blockPos = bufferPos;
    }

    public SForceBlockUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(blockPos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            var player = context.getSender();
            if (player != null) {
                var pos = new BlockPos(blockPos[0], blockPos[1], blockPos[2]);
                player.connection.send(new ClientboundBlockUpdatePacket(pos, player.level().getBlockState(pos)));
            }
        } else {
            context.setPacketHandled(false);
        }
    }
}
