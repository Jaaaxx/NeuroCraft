package com.dementia.neurocraft.common.features;

import net.minecraftforge.event.level.BlockEvent;

abstract public class FeatureBlockBreak extends Feature {
    protected FeatureBlockBreak(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, triggerType);
    }

    public abstract void onBlockBreak(BlockEvent.BreakEvent event);
}
