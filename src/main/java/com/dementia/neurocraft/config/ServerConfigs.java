package com.dementia.neurocraft.config;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureRegistry;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import com.dementia.neurocraft.network.CConfigValueSyncPacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;

public class ServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final Map<String, ForgeConfigSpec.ConfigValue<Boolean>> FEATURE_CONFIGS = new LinkedHashMap<>();

    public static final ForgeConfigSpec.ConfigValue<Boolean> PLAYER_SCALING;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SANITY_RESET_UPON_DEATH;
    public static final ForgeConfigSpec.ConfigValue<Integer> INITIAL_SANITY;
    public static final ForgeConfigSpec.ConfigValue<Integer> SCALING_INTERVAL;

    public static final HashMap<ForgeConfigSpec.ConfigValue<Integer>, Map.Entry<Integer, Integer>> RANGES = new HashMap<>();

    public static boolean isEnabled(String id) {
        ForgeConfigSpec.ConfigValue<Boolean> val = FEATURE_CONFIGS.get(id);
        return val != null && Boolean.TRUE.equals(val.get());
    }
    public static Map<String, ForgeConfigSpec.ConfigValue<Boolean>> getFeatureBooleanConfigs() {
        return FEATURE_CONFIGS;
    }
    public static ModConfig modConfig;

    /**
     * Collect all current config values into a map for syncing
     */
    public static Map<String, Object> getAllConfigValues() {
        Map<String, Object> values = new HashMap<>();
        
        // Add feature configs
        for (Map.Entry<String, ForgeConfigSpec.ConfigValue<Boolean>> entry : FEATURE_CONFIGS.entrySet()) {
            values.put(entry.getKey(), entry.getValue().get());
        }
        
        // Add other configs
        values.put("PLAYER_SCALING", PLAYER_SCALING.get());
        values.put("SANITY_RESET_UPON_DEATH", SANITY_RESET_UPON_DEATH.get());
        values.put("INITIAL_SANITY", INITIAL_SANITY.get());
        values.put("SCALING_INTERVAL", SCALING_INTERVAL.get());
        
        return values;
    }

    /**
     * Broadcast all config values to all connected players
     */
    public static void broadcastConfigValues() {
        MinecraftServer server = ServerLifecycleHooks.getCurrentServer();
        if (server == null) return;
        
        Map<String, Object> configValues = getAllConfigValues();
        CConfigValueSyncPacket packet = new CConfigValueSyncPacket(configValues);
        
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            PacketHandler.sendToPlayer(packet, player);
        }
    }

    /**
     * Send config values to a specific player (for when they join)
     */
    public static void sendConfigValuesToPlayer(ServerPlayer player) {
        Map<String, Object> configValues = getAllConfigValues();
        CConfigValueSyncPacket packet = new CConfigValueSyncPacket(configValues);
        PacketHandler.sendToPlayer(packet, player);
    }


    static {
        BUILDER.push("Server Configs for Neurocraft");
        // Add client feature configs using shared registry
        for (String featureId : FeatureRegistry.CLIENT_FEATURE_IDS) {
            if (FEATURE_CONFIGS.containsKey(featureId)) continue;

            ForgeConfigSpec.ConfigValue<Boolean> configValue = BUILDER.define(featureId, true);
            FEATURE_CONFIGS.put(featureId, configValue);
        }
        // Add server feature configs
        for (Feature feature : ServerFeatureController.getFeatures()) {
            if (FEATURE_CONFIGS.containsKey(feature.getId())) continue;

            ForgeConfigSpec.ConfigValue<Boolean> configValue = BUILDER.define(feature.getId(), true);
            FEATURE_CONFIGS.put(feature.getId(), configValue);
        }

        PLAYER_SCALING = BUILDER.define("PLAYER_SCALING", true);
        SANITY_RESET_UPON_DEATH = BUILDER.define("SANITY_RESET_UPON_DEATH", true);

        INITIAL_SANITY = BUILDER.defineInRange("INITIAL_SANITY", 1, 1, PEAK_SANITY);
        RANGES.put(INITIAL_SANITY, new AbstractMap.SimpleEntry<>(1, PEAK_SANITY));

        SCALING_INTERVAL = BUILDER.defineInRange("SCALING_INTERVAL", 10, 1, 20);
        RANGES.put(SCALING_INTERVAL, new AbstractMap.SimpleEntry<>(1, 20));

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
