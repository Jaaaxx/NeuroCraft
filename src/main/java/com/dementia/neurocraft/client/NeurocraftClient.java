package com.dementia.neurocraft.client;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = "neurocraft", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NeurocraftClient {
    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerHallucinations.getRandomName();
    }
}
