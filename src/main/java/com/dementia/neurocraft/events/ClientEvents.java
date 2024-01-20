package com.dementia.neurocraft.events;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.client.ClientHallucinations;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.mojang.text2speech.Narrator.LOGGER;

@Mod.EventBusSubscriber(modid = NeuroCraft.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
        ClientHallucinations.onClientTick(tick);
    }
}
