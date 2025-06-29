package com.dementia.neurocraft.config;

import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.network.CFeatureToggleUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.server.ServerLifecycleHooks;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.Neurocraft.LOGGER;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfigSyncHandler {
    @SubscribeEvent
    public static void onConfigLoaded(ModConfigEvent.Loading evt) {
        if (evt.getConfig().getSpec() == ServerConfigs.SPEC) {
            LOGGER.info("Server config loaded - syncing feature states");
            syncFeatureStates();
        }
    }

    @SubscribeEvent
    public static void onConfigReloaded(ModConfigEvent.Reloading evt) {
        if (evt.getConfig().getSpec() == ServerConfigs.SPEC) {
            LOGGER.info("Server config reloaded - syncing feature states");
            syncFeatureStates();
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            if (srv != null) {
                ServerFeatureController.broadcastFeatureStatesToClients(srv);
            }
        }
    }

    public static void syncFeatureStates() {
        LOGGER.info("=== SYNCING FEATURE STATES ===");
        
        // Debug: Print available config keys
        LOGGER.info("Available config keys: {}", ServerConfigs.FEATURE_CONFIGS.keySet());
        
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

        int serverSynced = 0;
        for (Feature f : ServerFeatureController.getFeatures()) {
            var cfg = ServerConfigs.FEATURE_CONFIGS.get(f.getId());
            if (cfg != null) {
                boolean enabled = Boolean.TRUE.equals(cfg.get());
                f.setEnabled(enabled);
                LOGGER.info("Server feature {} -> enabled: {}", f.getId(), enabled);
                serverSynced++;
            } else {
                LOGGER.warn("No config found for server feature: {}", f.getId());
            }
        }
        
        LOGGER.info("Synced {} client features and {} server features", clientSynced, serverSynced);

        if (FMLEnvironment.dist.isClient()) {
            var conn = Minecraft.getInstance().getConnection();
            if (conn != null && !Minecraft.getInstance().isLocalServer()) {
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
}
