package com.dementia.neurocraft.common.features;

import net.minecraftforge.event.level.BlockEvent;

abstract public class FeatureBlockPlace extends Feature {
    protected FeatureBlockPlace(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType, boolean canForceTrigger) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, FeatureTrigger.SERVER_BLOCK_PLACE, canForceTrigger);
    }

    public abstract void onBlockPlace(BlockEvent.EntityPlaceEvent event);
}
