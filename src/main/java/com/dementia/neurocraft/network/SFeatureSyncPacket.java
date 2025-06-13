package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.common.features.Feature;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

public class SFeatureSyncPacket {
    private final String featureId;
    private final boolean enabled;

    public SFeatureSyncPacket(String featureId, boolean enabled) {
        this.featureId = featureId;
        this.enabled = enabled;
    }

    public SFeatureSyncPacket(FriendlyByteBuf buf) {
        this.featureId = buf.readUtf();
        this.enabled = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(featureId);
        buf.writeBoolean(enabled);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        if (!ctx.isClientSide()) {
            ctx.setPacketHandled(false);
            return;
        }

        ctx.enqueueWork(() -> {
            for (Feature feature : ClientFeatureController.getFeatures()) {
                if (feature.getId().equals(featureId)) {
                    feature.setEnabled(enabled);
                }
            }
        });
    }
}
