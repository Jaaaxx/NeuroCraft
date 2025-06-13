package com.dementia.neurocraft.client.hooks;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.client.internal.HallucinationRenderHijack;
import com.dementia.neurocraft.client.internal.PlayerHallucinations;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import static com.dementia.neurocraft.Neurocraft.LOGGER;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID, value = Dist.CLIENT)
public final class ClientHooks {

    private static boolean injected = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (injected || event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getInstance().level == null) return;
        LOGGER.info("Attempting to hijack render system!");
        HallucinationRenderHijack.apply();
        injected = true;
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        PlayerHallucinations.getRandomName();
    }
}
