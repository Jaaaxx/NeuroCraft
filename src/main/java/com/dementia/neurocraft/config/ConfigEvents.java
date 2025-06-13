package com.dementia.neurocraft.config;

import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "neurocraft", bus = Mod.EventBusSubscriber.Bus.MOD)
public class ConfigEvents {

    @SubscribeEvent
    public static void onModConfigEvent(ModConfigEvent.Loading event) {
        if (event.getConfig().getSpec() == ServerConfigs.SPEC) {
            ServerConfigs.modConfig = event.getConfig();
        }
    }

    @SubscribeEvent
    public static void onModConfigReloading(ModConfigEvent.Reloading event) {
        if (event.getConfig().getSpec() == ServerConfigs.SPEC) {
            // Optional: do something on config reload
        }
    }
}
