package com.dementia.neurocraft.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ClientConfigs {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;


    public static final ForgeConfigSpec.ConfigValue<Boolean> HALLUCINATION_SFX;
    public static final ForgeConfigSpec.ConfigValue<Boolean> SANITY_GUI;

    static {
        BUILDER.push("Client Configs for Neurocraft");

        HALLUCINATION_SFX = BUILDER.define("HALLUCINATION_SFX", true);
        SANITY_GUI = BUILDER.define("SANITY_GUI", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
