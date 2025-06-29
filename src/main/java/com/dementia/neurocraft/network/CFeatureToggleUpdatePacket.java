package com.dementia.neurocraft.network;

import com.dementia.neurocraft.config.ConfigSyncHandler;
import com.dementia.neurocraft.config.ServerConfigs;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.config.ModConfig;

public class CFeatureToggleUpdatePacket {
    private final String featureId;
    private final boolean enabled;

    public CFeatureToggleUpdatePacket(String featureId, boolean enabled) {
        this.featureId = featureId;
        this.enabled = enabled;
    }

    public CFeatureToggleUpdatePacket(FriendlyByteBuf buf) {
        this.featureId = buf.readUtf();
        this.enabled = buf.readBoolean();
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeUtf(featureId);
        buf.writeBoolean(enabled);
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        if (!ctx.isServerSide()) {
            ctx.setPacketHandled(false);
            return;
        }

        ctx.enqueueWork(() -> {
            var player = ctx.getSender();
            if (player == null || !player.hasPermissions(2)) return;

            var configValue = ServerConfigs.FEATURE_CONFIGS.get(featureId);
            if (configValue != null) {
                ModConfig cfg = ServerConfigs.modConfig;
                if (cfg != null) {
                    configValue.set(enabled);
                    cfg.save();
                    cfg.getSpec().afterReload();
                    ConfigSyncHandler.syncFeatureStates();
                }


                ServerFeatureController.getFeatureById(featureId)
                        .ifPresent(feature -> feature.setEnabled(enabled));

                ServerFeatureController.broadcastFeatureStatesToClients(player.getServer());
                // Broadcast all config values to all players
                ServerConfigs.broadcastConfigValues();
            }
        });
    }
}
