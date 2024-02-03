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

import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;
import static com.dementia.neurocraft.server.ServerHallucinations.getPlayerEntities;

public class SUpdatePlayerSanityPacket {
    public SUpdatePlayerSanityPacket() {
    }

    public SUpdatePlayerSanityPacket(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            PacketHandler.sendToPlayer(new CUpdatePlayerSanityPacket(getPlayerSanity(player)), player);
            context.setPacketHandled(true);
        } else {
            context.setPacketHandled(false);
        }
    }

    public static int[] toIntArray(BlockPos bp) {
        return new int[]{bp.getX(), bp.getY(), bp.getZ()};
    }

}
