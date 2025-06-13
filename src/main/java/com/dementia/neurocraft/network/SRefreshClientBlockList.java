package com.dementia.neurocraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SRefreshClientBlockList {
    private final int[] blockPos;


    public SRefreshClientBlockList(int[] blockPos) {
        this.blockPos = blockPos;
    }

    public SRefreshClientBlockList(FriendlyByteBuf buffer) {
        this(buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(blockPos);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            BlockPos bp = new BlockPos(blockPos[0], blockPos[1], blockPos[2]);
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            Level level = player.level();
            BlockState bs = level.getBlockState(bp);

            ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(bp, bs);
            player.connection.send(packet);
            context.setPacketHandled(true);
        } else {
            context.setPacketHandled(false);
        }
    }

    public static int[] toIntArray(BlockPos bp) {
        return new int[]{bp.getX(), bp.getY(), bp.getZ()};
    }

}