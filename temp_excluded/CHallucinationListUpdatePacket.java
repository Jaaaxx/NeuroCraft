package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.client.features.impl.EnemyHallucination;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.ArrayList;
import java.util.List;

public class CHallucinationListUpdatePacket {

    private final int[] entityIDList;

    public CHallucinationListUpdatePacket(int[] ids) {
        this.entityIDList = ids;
    }

    public CHallucinationListUpdatePacket(FriendlyByteBuf buf) {
        this(buf.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeVarIntArray(entityIDList);
    }

    /** Client-side handling: replace the current hallucination list. */
    public void handle(CustomPayloadEvent.Context ctx) {
        if (!ctx.isClientSide()) {
            ctx.setPacketHandled(false);
            return;
        }

        ctx.enqueueWork(() -> {
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            List<Entity> hallucinations = new ArrayList<>();
            for (int id : entityIDList) {
                Entity e = mc.player.level().getEntity(id);
                if (e != null) hallucinations.add(e);
            }

            ClientFeatureController.getFeatureById("ENEMY_HALLUCINATIONS").ifPresent(f ->  {
                if (f instanceof EnemyHallucination eh) {
                    eh.syncHallucinations(hallucinations);
                }
            });

        });
    }
}
