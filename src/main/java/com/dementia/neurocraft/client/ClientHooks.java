package com.dementia.neurocraft.client;

import com.dementia.neurocraft.Neurocraft;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID, value = Dist.CLIENT)
public final class ClientHooks {

    private static boolean injected = false;

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END || injected) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.level != null) {
            EntityRendererInterceptor.injectTextureAdvice();
            injected = true;
        }
    }
}
