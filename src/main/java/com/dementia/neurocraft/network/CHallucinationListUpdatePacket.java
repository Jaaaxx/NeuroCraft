package com.dementia.neurocraft.network;

import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;

import static com.dementia.neurocraft.client.ClientHallucinations.playerEntities;

public class CHallucinationListUpdatePacket {
    private final int[] entityIDList;

    public CHallucinationListUpdatePacket(int[] entityIDList) {
        this.entityIDList = entityIDList;
    }

    public CHallucinationListUpdatePacket(FriendlyByteBuf buffer) {
        this(buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeVarIntArray(entityIDList);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isClientSide()) {
            var player = Minecraft.getInstance().player;
            var entities = new ArrayList<Entity>();

            for (var id : entityIDList) {
                if (player != null) {
                    entities.add(player.level().getEntity(id));
                }
            }
            playerEntities = entities;
        } else {
            context.setPacketHandled(false);
        }
    }
}
