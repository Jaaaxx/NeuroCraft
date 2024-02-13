package com.dementia.neurocraft.util;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.AbstractMap;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.dementia.neurocraft.Neurocraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientTimingHandler {
    private static final int maxTicks = 2000;
    private static int tickCount = 0;
    private static final Map<Map.Entry<Map.Entry<String, Integer>, Map.Entry<Runnable, Boolean>>,Integer> _scheduledEvents = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ClientTickEvent event) {
        if (event.side != LogicalSide.CLIENT || event.type != TickEvent.Type.CLIENT)
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