package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.FeatureSleepInBed;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;
import net.minecraftforge.eventbus.api.Event;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;


public final class AbleToSleep extends FeatureSleepInBed {
    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private SleepingLocationCheckEvent currentEvent;

    public AbleToSleep() {
        super("ABLE_TO_SLEEP", "Able to Sleep", 0, 1, 0, true, FeatureTrigger.SLEEP_IN_BED, false);
    }

    @Override
    public void performServer(ServerPlayer player) {
        this.currentEvent.setResult(Event.Result.DENY);
    }

    @Override
    public void OnSleepEvent(SleepingLocationCheckEvent event) {
        this.currentEvent = event;
    }
}
