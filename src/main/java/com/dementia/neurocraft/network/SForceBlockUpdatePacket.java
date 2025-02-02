package com.dementia.neurocraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

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

    public void handle(Supplier<NetworkEvent.Context> context) {
        var player = context.get().getSender();
        if (player != null) {
            var pos = new BlockPos(blockPos[0], blockPos[1], blockPos[2]);
            player.connection.send(new ClientboundBlockUpdatePacket(pos, player.level().getBlockState(pos)));
        }
    }
}
