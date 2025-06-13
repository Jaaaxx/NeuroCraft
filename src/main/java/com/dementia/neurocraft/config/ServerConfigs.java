package com.dementia.neurocraft.config;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

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
    public static ModConfig modConfig; // reference for saving


    static {
        BUILDER.push("Server Configs for Neurocraft");

        // Dynamically define config entries for each Feature
        for (Feature feature : ClientFeatureController.getFeatures()) {
            if (FEATURE_CONFIGS.containsKey(feature.getId())) continue;   // already defined

            ForgeConfigSpec.ConfigValue<Boolean> configValue = BUILDER.define(feature.getId(), true);
            FEATURE_CONFIGS.put(feature.getId(), configValue);
        }
        for (Feature feature : ServerFeatureController.getFeatures()) {
            if (FEATURE_CONFIGS.containsKey(feature.getId())) continue;   // already defined

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
