package com.dementia.neurocraft.common.features;


import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

abstract public class FeatureLivingEntityFinishUseItem extends Feature {
    protected FeatureLivingEntityFinishUseItem(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType, boolean canForceTrigger) {
        super(id, displayName, sanityThreshold, maxTriggerChance, secondsInterval, enabled, FeatureTrigger.SERVER_FINISH_USE_ITEM, canForceTrigger);
    }

    public abstract void onFinishUseItemEvent(LivingEntityUseItemEvent.Finish event);
}
