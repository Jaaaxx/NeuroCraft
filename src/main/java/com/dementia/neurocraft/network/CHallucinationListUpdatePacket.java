package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.internal.EnemyHallucinationClientHandler;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class CHallucinationListUpdatePacket {
    private final List<Integer> entityIds;

    public CHallucinationListUpdatePacket(int[] entityIds) {
        this.entityIds = new ArrayList<>();
        for (int id : entityIds) {
            this.entityIds.add(id);
        }
    }

    public CHallucinationListUpdatePacket(List<Integer> entityIds) {
        this.entityIds = entityIds;
    }

    public CHallucinationListUpdatePacket(FriendlyByteBuf buf) {
        int size = buf.readInt();
        this.entityIds = new ArrayList<>();
        for (int i = 0; i < size; i++) {
            this.entityIds.add(buf.readInt());
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityIds.size());
        for (int id : entityIds) {
            buf.writeInt(id);
        }
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        if (!ctx.isClientSide()) {
            ctx.setPacketHandled(false);
            return;
        }

        ctx.enqueueWork(() -> {
            EnemyHallucinationClientHandler.updateHallucinations(entityIds);
        });
        
        ctx.setPacketHandled(true);
    }
} 