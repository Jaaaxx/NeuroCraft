package com.dementia.neurocraft.common.features;

import net.minecraftforge.event.entity.player.SleepingLocationCheckEvent;

abstract public class FeatureSleepInBed extends Feature {
    protected FeatureSleepInBed(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType, boolean canForceTrigger) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, FeatureTrigger.SLEEP_IN_BED, canForceTrigger);
    }

    public abstract void OnSleepEvent(SleepingLocationCheckEvent event);
}
