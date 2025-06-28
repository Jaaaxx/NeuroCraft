package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.world.item.ItemStack;

import static com.dementia.neurocraft.client.internal.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;
import static com.dementia.neurocraft.client.internal.InventoryUtils.*;

public final class ItemTypeDementia extends Feature {

    public ItemTypeDementia() {
        super("REPLACE_ITEMS_IN_INVENTORY", "Item Type Dementia", 150, 0.3, 30, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performClient(Minecraft mc) {
        var inventory = mc.player.getInventory();
        int slotIndex = getRandomNonEmptySlotIndex(inventory);
        if (slotIndex == -1) return;

        ItemStack original = inventory.getItem(slotIndex);
        inventory.setItem(slotIndex, new ItemStack(getRandomItem(), original.getCount()));

        HallucinationOccuredClient();
    }
}
