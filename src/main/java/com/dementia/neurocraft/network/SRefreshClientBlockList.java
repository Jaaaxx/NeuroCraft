package com.dementia.neurocraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.List;

import static com.dementia.neurocraft.server.ServerHallucinations.getPlayerEntities;

public class SRefreshClientBlockList {
    private final int[] blockPos;
    private final int itemID;

    public SRefreshClientBlockList(int[] blockPos, int itemID) {
        this.blockPos = blockPos;
        this.itemID = itemID;
    }

    public SRefreshClientBlockList(FriendlyByteBuf buffer) {
        this(buffer.readVarIntArray(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(blockPos);
        buffer.writeInt(itemID);
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
            PacketHandler.sendVanillaPacket(packet, player);

            player.addItem(new ItemStack(Item.byId(itemID), 1));
        } else {
            context.setPacketHandled(false);
        }
    }

    public static int[] toIntArray(BlockPos bp) {
        return new int[]{bp.getX(), bp.getY(), bp.getZ()};
    }

}
