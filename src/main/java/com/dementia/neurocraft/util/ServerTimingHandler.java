package com.dementia.neurocraft.util;

import com.dementia.neurocraft.NeuroCraft;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

@Mod.EventBusSubscriber(modid = NeuroCraft.MODID)
public class ServerTimingHandler {
    private static final int maxTicks = 2000;
    private static int tickCount = 0;
    private static final Map<Map.Entry<Map.Entry<String, Integer>, Map.Entry<Runnable, Boolean>>,Integer> _scheduledEvents = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.side != LogicalSide.SERVER || event.getServer() == null || event.type != TickEvent.Type.SERVER)
            return;
        if (event.phase == TickEvent.Phase.END) {
            Iterator<Map.Entry<Map.Entry<Map.Entry<String, Integer>, Map.Entry<Runnable, Boolean>>, Integer>> iterator = _scheduledEvents.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<Map.Entry<Map.Entry<String, Integer>, Map.Entry<Runnable, Boolean>>, Integer> entry = iterator.next();

                String name = entry.getKey().getKey().getKey();
                int maxTicks = entry.getKey().getKey().getValue();
                Runnable task = entry.getKey().getValue().getKey();
                Boolean runOnce = entry.getKey().getValue().getValue();
                int ticksRemaining = entry.getValue();
                entry.setValue(++ticksRemaining);

                if (ticksRemaining == maxTicks) {
                    task.run();
                    if (runOnce) {
                        iterator.remove();
                    }
                }
            }
            if (tickCount++ >= maxTicks) {
                tickCount = 0;
            }
        }
    }

    public static void scheduleEvent(String name, int ticks, Runnable task, boolean runsOnce) {
        _scheduledEvents.put(new AbstractMap.SimpleEntry<>(new AbstractMap.SimpleEntry<>(name, ticks), new AbstractMap.SimpleEntry<>(task, runsOnce)), 0);
    }
}