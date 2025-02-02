package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

import static com.dementia.neurocraft.server.ServerHallucinations.getPlayerEntities;

public class SRemoveHallucinationPacket {
    private final Integer entityID;

    public SRemoveHallucinationPacket(Integer entityID) {
        this.entityID = entityID;
    }

    public SRemoveHallucinationPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(entityID);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        var player = context.get().getSender();
        if (player != null) {
            var entity = player.level().getEntity(entityID);
            if (entity != null) {
                getPlayerEntities(player).remove(entity);
                entity.remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }
}
