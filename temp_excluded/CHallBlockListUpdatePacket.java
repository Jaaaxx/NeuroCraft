package com.dementia.neurocraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;

import static com.dementia.neurocraft.client.internal.ClientBlockVerify.*;

public class CHallBlockListUpdatePacket {
    private final int[] blockPosList;
    // TODO FIX
    public CHallBlockListUpdatePacket(int[] blockPosList) {
        this.blockPosList = blockPosList;
    }

    public CHallBlockListUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(blockPosList);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isClientSide()) {
            var player = Minecraft.getInstance().player;
            if (player == null)
                return;
            addToHallucinationBlocks(decode(blockPosList));
            var hallBlocks = getHallucinationBlocks();
            if (hallBlocks.size() >= 5) {
                var blockPos = hallBlocks.get(0);
                removeHallucinationBlocks(blockPos);
            }
            context.setPacketHandled(true);
        } else {
            context.setPacketHandled(false);
        }
    }

    public static ArrayList<BlockPos> decode(int[] positions) {
        ArrayList<BlockPos> blockList = new ArrayList<>();
        int x = 0, y = 0, z;
        int c = 1;
        for (int num : positions) {
            if (c % 3 == 1)
                x = num;
            if (c % 3 == 2)
                y = num;
            if (c % 3 == 0) {
                z = num;
                blockList.add(new BlockPos(x, y, z));
            }
            c++;
        }
        return blockList;
    }
}