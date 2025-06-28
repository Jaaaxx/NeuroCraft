package com.dementia.neurocraft.common.features;

import net.minecraftforge.event.level.BlockEvent;

abstract public class FeatureBlockBreak extends Feature {
    protected FeatureBlockBreak(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType, boolean canForceTrigger) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, FeatureTrigger.SERVER_BLOCK_BREAK, canForceTrigger);
    }

    public abstract void onBlockBreak(BlockEvent.BreakEvent event);
}
