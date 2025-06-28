package com.dementia.neurocraft.common.features;

import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;

abstract public class FeatureClientMobSpawn extends Feature {
    protected FeatureClientMobSpawn(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType, boolean canForceTrigger) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, FeatureTrigger.CLIENT_MOB_SPAWN, canForceTrigger);
    }

    public abstract void onMobSpawn(EntityJoinLevelEvent event);
}
