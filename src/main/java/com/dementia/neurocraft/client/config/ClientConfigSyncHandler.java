package com.dementia.neurocraft.client.config;

import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.config.ServerConfigs;
import com.dementia.neurocraft.network.CFeatureToggleUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.Neurocraft.LOGGER;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class ClientConfigSyncHandler {
    
    @SubscribeEvent
    public static void onConfigLoaded(ModConfigEvent.Loading evt) {
        if (evt.getConfig().getSpec() == ServerConfigs.SPEC) {
            LOGGER.info("Client: Server config loaded - syncing client feature states");
            syncClientFeatureStates();
        }
    }

    @SubscribeEvent
    public static void onConfigReloaded(ModConfigEvent.Reloading evt) {
        if (evt.getConfig().getSpec() == ServerConfigs.SPEC) {
            LOGGER.info("Client: Server config reloaded - syncing client feature states");
            syncClientFeatureStates();
            sendConfigToServer();
        }
    }

    /**
     * Sync feature states from server configs to client features
     */
    public static void syncClientFeatureStates() {
        LOGGER.info("Syncing client feature states from config");
        
        int clientSynced = 0;
        for (Feature f : ClientFeatureController.getFeatures()) {
            var cfg = ServerConfigs.FEATURE_CONFIGS.get(f.getId());
            if (cfg != null) {
                boolean enabled = Boolean.TRUE.equals(cfg.get());
                f.setEnabled(enabled);
                LOGGER.info("Client feature {} -> enabled: {}", f.getId(), enabled);
                clientSynced++;
            } else {
                LOGGER.warn("No config found for client feature: {}", f.getId());
            }
        }
        
        LOGGER.info("Synced {} client features", clientSynced);
    }

    /**
     * Send client config changes to server
     */
    private static void sendConfigToServer() {
        var conn = Minecraft.getInstance().getConnection();
        if (conn != null && !Minecraft.getInstance().isLocalServer()) {
            LOGGER.info("Sending client config changes to server");
            for (Feature f : ClientFeatureController.getFeatures()) {
                var cfg = ServerConfigs.FEATURE_CONFIGS.get(f.getId());
                if (cfg != null) {
                    PacketHandler.sendToServer(
                            new CFeatureToggleUpdatePacket(f.getId(), cfg.get()));
                }
            }
        }
    }
} 