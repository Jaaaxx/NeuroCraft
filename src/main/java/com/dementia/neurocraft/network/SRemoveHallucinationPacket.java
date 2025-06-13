package com.dementia.neurocraft.network;

import com.dementia.neurocraft.server.features.ServerFeatureController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SRemoveHallucinationPacket {
    private final int entityID;

    public SRemoveHallucinationPacket(int id) {
        this.entityID = id;
    }

    public SRemoveHallucinationPacket(FriendlyByteBuf buf) {
        this(buf.readInt());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(entityID);
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (!context.isServerSide()) {
            context.setPacketHandled(false);
            return;
        }

        var player = context.getSender();
        if (player == null) return;

        var tracker = ServerFeatureController.getHallucinationTracker();
        var hallucinations = tracker.get(player);


        hallucinations.removeIf(id -> id == entityID);

        // Remove the entity from the world if it still exists
        Entity entity = player.level().getEntity(entityID);
        if (entity != null) {
            entity.remove(Entity.RemovalReason.DISCARDED);
        }
    }
}
