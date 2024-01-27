package com.dementia.neurocraft.events;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.client.ClientHallucinations;
import net.minecraft.client.Minecraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = NeuroCraft.MODID)
public class ClientEvents {
    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
        if (tick.side == LogicalSide.CLIENT) {
            ClientHallucinations.onClientTick(tick);
        }
    }
}
