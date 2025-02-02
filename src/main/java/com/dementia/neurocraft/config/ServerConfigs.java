package com.dementia.neurocraft.config;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Map;

public class ServerConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.ConfigValue<Boolean> FAKE_PLACE_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FAKE_BREAK_BLOCKS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ENEMY_HALLUCINATIONS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ITEMS_LOSE_LETTERS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> REPLACE_ITEMS_IN_INVENTORY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ITEMS_SWAP_POSITIONS_IN_INVENTORY;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OPTION_FOV_CHANGES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OPTION_BRIGHTNESS_CHANGES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OPTION_FRAMERATE_CHANGES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OPTION_CONTROL_SWAPS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OPTION_RENDER_DISTANCE_CHANGES;
    public static final ForgeConfigSpec.ConfigValue<Boolean> OPTION_SCHITZOWORLD;
    public static final ForgeConfigSpec.ConfigValue<Boolean> AUDITORY_HALLUCINATIONS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FOOD_HALLUCINATIONS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> FURNACE_UNCOOKING;
    public static final ForgeConfigSpec.ConfigValue<Boolean> ORE_HALLUCINATIONS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RANDOM_TELEPORTING;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RANDOMIZE_BARS;
    public static final ForgeConfigSpec.ConfigValue<Boolean> RANDOMIZE_XP;

    // Sanity options
    public static final ForgeConfigSpec.ConfigValue<Boolean> PLAYER_SCALING;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SANITY_RESET_UPON_DEATH;
    static public final ForgeConfigSpec.ConfigValue<Integer> PEAK_SANITY;
    static public final ForgeConfigSpec.ConfigValue<Integer> INITIAL_SANITY;
    static public final ForgeConfigSpec.ConfigValue<Integer> SCALING_INTERVAL; // seconds
    static public final ForgeConfigSpec.ConfigValue<Integer> SCALING_INCREMENT;

    public static final HashMap<ForgeConfigSpec.ConfigValue<Integer>, Map.Entry<Integer, Integer>> RANGES = new HashMap<>();

    static {
        BUILDER.push("Server Configs for Neurocraft");

        FAKE_PLACE_BLOCKS = BUILDER.define("FAKE_PLACE_BLOCKS", true);
        FAKE_BREAK_BLOCKS = BUILDER.define("FAKE_BREAK_BLOCKS", true);
        ENEMY_HALLUCINATIONS = BUILDER.define("ENEMY_HALLUCINATIONS", true);
        ITEMS_LOSE_LETTERS = BUILDER.define("ITEMS_LOSE_LETTERS", true);
        REPLACE_ITEMS_IN_INVENTORY = BUILDER.define("REPLACE_ITEMS_IN_INVENTORY", true);
        ITEMS_SWAP_POSITIONS_IN_INVENTORY = BUILDER.define("ITEMS_SWAP_POSITIONS_IN_INVENTORY", true);
        OPTION_FOV_CHANGES = BUILDER.define("OPTION_FOV_CHANGES", true);
        OPTION_BRIGHTNESS_CHANGES = BUILDER.define("OPTION_BRIGHTNESS_CHANGES", true);
        OPTION_FRAMERATE_CHANGES = BUILDER.define("OPTION_FRAMERATE_CHANGES", true);
        OPTION_CONTROL_SWAPS = BUILDER.define("OPTION_CONTROL_SWAPS", true);
        OPTION_RENDER_DISTANCE_CHANGES = BUILDER.define("OPTION_RENDER_DISTANCE_CHANGES", true);
        OPTION_SCHITZOWORLD = BUILDER.define("OPTION_SCHITZOWORLD", true);
        AUDITORY_HALLUCINATIONS = BUILDER.define("AUDITORY_HALLUCINATIONS", true);
        FOOD_HALLUCINATIONS = BUILDER.define("FOOD_HALLUCINATIONS", true);
        FURNACE_UNCOOKING = BUILDER.define("FURNACE_UNCOOKING", true);
        ORE_HALLUCINATIONS = BUILDER.define("ORE_HALLUCINATIONS", true);
        RANDOM_TELEPORTING = BUILDER.define("RANDOM_TELEPORTING", true);
        RANDOMIZE_BARS = BUILDER.define("RANDOMIZE_BARS", true);
        RANDOMIZE_XP = BUILDER.define("RANDOMIZE_XP", true);


        PLAYER_SCALING = BUILDER.define("PLAYER_SCALING", true);
        SANITY_RESET_UPON_DEATH = BUILDER.define("SANITY_RESET_UPON_DEATH", true);

        int highest_sanity = 1600;
        PEAK_SANITY = BUILDER.defineInRange("PEAK_SANITY", 800, 100, highest_sanity);
        RANGES.put(PEAK_SANITY, new AbstractMap.SimpleEntry<>(100, highest_sanity));

        INITIAL_SANITY = BUILDER.defineInRange("INITIAL_SANITY", 1, 1, highest_sanity);
        RANGES.put(INITIAL_SANITY, new AbstractMap.SimpleEntry<>(1, 800));

        SCALING_INTERVAL = BUILDER.defineInRange("SCALING_INTERVAL", 30, 1, 120);
        RANGES.put(SCALING_INTERVAL, new AbstractMap.SimpleEntry<>(1, 120));

        SCALING_INCREMENT = BUILDER.defineInRange("SCALING_INCREMENT", 1, 1, 30);
        RANGES.put(SCALING_INCREMENT, new AbstractMap.SimpleEntry<>(1, 30));


        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
