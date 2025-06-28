package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.FeatureContainerOpen;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;

import java.util.List;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;

public final class FurnaceUncooking extends FeatureContainerOpen {
    private PlayerContainerEvent.Open event;

    public FurnaceUncooking() {
        super("FURNACE_UNCOOKING", "Furnace Uncooking",
                300, 0.2, 0, true, FeatureTrigger.SERVER_CONTAINER_OPEN, false);
    }

    @Override
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        this.event = event;
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (event == null) return;

        var container = event.getContainer();
        var pool = List.of(MenuType.FURNACE, MenuType.SMOKER);

        if (!pool.contains(container.getType())) return;

        var ingredientSlot = container.getSlot(0);
        var ingItem = ingredientSlot.getItem();

        var fuelSlot = container.getSlot(1);
        var fuelItem = fuelSlot.getItem();

        var resultSlot = container.getSlot(2);
        var resultItem = resultSlot.getItem();

        if (resultItem.isEmpty()) return;
        if (ingItem.isEmpty()) return;

        resultSlot.set(ItemStack.EMPTY);
        ingredientSlot.set(ItemStack.EMPTY);
        player.getInventory().add(new ItemStack(ingItem.getItem(), resultItem.getCount() + ingItem.getCount()));

        if (!fuelItem.isEmpty()) {
            fuelSlot.set(ItemStack.EMPTY);
            player.getInventory().add(fuelItem);
        }

        container.setData(0, 0); // set furnace off

        HallucinationOccured(player);
        this.event = null;
    }
}
