package com.dementia.neurocraft.common.features;


import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

abstract public class FeatureLivingEntityFinishUseItem extends Feature {
    protected FeatureLivingEntityFinishUseItem(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, triggerType);
    }

    public abstract void onFinishUseItemEvent(LivingEntityUseItemEvent.Finish event);
}
