package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import static com.dementia.neurocraft.client.internal.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;
import static com.dementia.neurocraft.client.internal.InventoryUtils.*;

public final class ItemNameDementia extends Feature {

    public ItemNameDementia() {
        super("ITEMS_LOSE_LETTERS", "Item Name Dementia", 150, 0.4, 2, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performClient(Minecraft mc) {
        var inventory = mc.player.getInventory();
        int slotIndex = getRandomNonEmptySlotIndex(inventory);
        if (slotIndex == -1) return;

        ItemStack itemStack = inventory.getItem(slotIndex);
        String oldName = itemStack.getHoverName().getString();
        String newName = removeRandomChar(oldName);

        if (!newName.isEmpty() && !containsObfuscatedText(oldName)) {
            itemStack.setHoverName(Component.literal(newName));
        } else {
            itemStack.setHoverName(Component.literal(ChatFormatting.OBFUSCATED + createRandomLengthA()));
        }

        HallucinationOccuredClient();
    }
}
