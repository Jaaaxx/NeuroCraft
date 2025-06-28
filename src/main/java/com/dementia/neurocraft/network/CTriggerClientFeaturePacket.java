package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.common.features.Feature;
import net.minecraft.client.Minecraft;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import java.util.Optional;

public class CTriggerClientFeaturePacket {
    private final String featureId;

    public CTriggerClientFeaturePacket(String featureId) {
        this.featureId = featureId;
    }

    public CTriggerClientFeaturePacket(FriendlyByteBuf buffer) {
        this.featureId = buffer.readUtf();
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeUtf(this.featureId);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        if (!ctx.isClientSide()) {
            ctx.setPacketHandled(false);
            return;
        }

        ctx.enqueueWork(() -> {
            // This runs on the client side
            Minecraft mc = Minecraft.getInstance();
            if (mc.player == null) return;

            Optional<Feature> feature = ClientFeatureController.getFeatureById(this.featureId);
            if (feature.isPresent()) {
                feature.get().performClient(mc);
            }
        });
    }
} 