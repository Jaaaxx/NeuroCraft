package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.FeatureLivingEntityFinishUseItem;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;

public final class EatingDisorder extends FeatureLivingEntityFinishUseItem {

    private LivingEntityUseItemEvent.Finish event;

    public EatingDisorder() {
        super("EATING_DISORDER", "Eating Disorder",
                300,    // sanity threshold
                0.3,    // trigger chance
                0,      // event-based
                true,
                FeatureTrigger.FINISH_USE_ITEM);
    }

    @Override
    public void onFinishUseItemEvent(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer)) return;
        this.event = event;
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (event == null) return;

        var foodProps = event.getResultStack().getFoodProperties(player);
        if (foodProps != null) {
            var foodData = player.getFoodData();
            foodData.setFoodLevel(Math.max(0, foodData.getFoodLevel() - foodProps.getNutrition() + 1));
            foodData.setSaturation(0);
        }

        var stack = event.getResultStack();
        stack.setCount(Math.max(0, stack.getCount() - 2));
        event.setResultStack(stack);

        HallucinationOccured(player);
        event = null;
    }
}
