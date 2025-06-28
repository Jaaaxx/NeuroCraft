package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.dementia.neurocraft.Neurocraft.LOGGER;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;

public final class InventoryDisarray extends Feature {

    public InventoryDisarray() {
        super("ITEMS_SWAP_POSITIONS_IN_INVENTORY", "Inventory Disarray", 200, 0.15, 60, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performServer(ServerPlayer player) {
        randomizePlayerInventory(player.getInventory());
        LOGGER.info("Inventory randomized for " + player.getScoreboardName());
        HallucinationOccured(player, false, false);
    }

    private void randomizePlayerInventory(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>(36);

        for (int i = 0; i < 36; i++) {
            items.add(inventory.getItem(i));
            inventory.setItem(i, ItemStack.EMPTY);
        }

        Collections.shuffle(items, new Random());

        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, items.get(i));
        }
    }
}
