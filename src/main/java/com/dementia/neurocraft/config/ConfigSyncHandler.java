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

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class ConfigSyncHandler {

    /* ─────────────────────────────────────────────────────────────────── */
    /*  CONFIG EVENTS                                                     */
    /* ─────────────────────────────────────────────────────────────────── */

    @SubscribeEvent
    public static void onConfigLoaded(ModConfigEvent.Loading evt) {
        if (evt.getConfig().getSpec() == ServerConfigs.SPEC) {
            syncFeatureStates();          // first-time load (startup)
        }
    }

    @SubscribeEvent
    public static void onConfigReloaded(ModConfigEvent.Reloading evt) {
        if (evt.getConfig().getSpec() == ServerConfigs.SPEC) {
            syncFeatureStates();          // hot reload
            MinecraftServer srv = ServerLifecycleHooks.getCurrentServer();
            if (srv != null) {
                ServerFeatureController.broadcastFeatureStatesToClients(srv);
            }
        }
    }

    /* ─────────────────────────────────────────────────────────────────── */
    /*  RUNTIME SYNC                                                      */
    /* ─────────────────────────────────────────────────────────────────── */

    public static void syncFeatureStates() {
        /* ------------ CLIENT FEATURES ------------- */
        for (Feature f : ClientFeatureController.getFeatures()) {
            var cfg = ServerConfigs.FEATURE_CONFIGS.get(f.getId());
            if (cfg != null) f.setEnabled(Boolean.TRUE.equals(cfg.get()));
        }

        /* ------------ SERVER FEATURES ------------- */
        for (Feature f : ServerFeatureController.getFeatures()) {
            var cfg = ServerConfigs.FEATURE_CONFIGS.get(f.getId());
            if (cfg != null) f.setEnabled(Boolean.TRUE.equals(cfg.get()));
        }

        /* ---------- SEND TO SERVER (CLIENT) ------- */
        if (FMLEnvironment.dist.isClient()) {
            var conn = Minecraft.getInstance().getConnection();
            if (conn != null && !Minecraft.getInstance().isLocalServer()) {      // remote client-side only
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
