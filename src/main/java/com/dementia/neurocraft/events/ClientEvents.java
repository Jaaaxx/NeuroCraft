package com.dementia.neurocraft.events;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.client.ClientHallucinations;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
        if (tick.side == LogicalSide.CLIENT) {
            ClientHallucinations.onClientTick(tick);
        }
    }
}
