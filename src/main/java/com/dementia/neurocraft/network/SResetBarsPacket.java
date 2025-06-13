package com.dementia.neurocraft.network;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SResetBarsPacket {
    public SResetBarsPacket() {
    }

    public SResetBarsPacket(FriendlyByteBuf buffer) {
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isServerSide()) {
            ServerPlayer player = context.getSender();
            if (player == null)
                return;
            var foodLevel = player.getFoodData().getFoodLevel();
            var health = player.getHealth();
            PacketHandler.sendToPlayer(new CResetBarsPacket(foodLevel, health), player);
            context.setPacketHandled(true);
        } else {
            context.setPacketHandled(false);
        }
    }

    public static int[] toIntArray(BlockPos bp) {
        return new int[]{bp.getX(), bp.getY(), bp.getZ()};
    }

}
