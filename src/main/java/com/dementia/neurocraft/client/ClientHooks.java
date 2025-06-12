package com.dementia.neurocraft.client;

import com.dementia.neurocraft.Neurocraft;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.LOGGER;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID, value = Dist.CLIENT)
public final class ClientHooks {

    private static boolean injected = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (injected || event.phase != TickEvent.Phase.END) return;
        if (Minecraft.getInstance().level == null) return;
        LOGGER.info("Attempting to hijack render system!");
        HallucinationRenderHijack.apply();   // run once
        injected = true;
    }
}
