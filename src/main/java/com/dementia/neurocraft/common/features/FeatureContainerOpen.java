package com.dementia.neurocraft.common.features;

import net.minecraftforge.event.entity.player.PlayerContainerEvent;

abstract public class FeatureContainerOpen extends Feature {
    protected FeatureContainerOpen(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType, boolean canForceTrigger) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, FeatureTrigger.SERVER_CONTAINER_OPEN, canForceTrigger);
    }

    public abstract void onContainerOpen(PlayerContainerEvent.Open event);
}
