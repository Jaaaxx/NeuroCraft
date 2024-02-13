package com.dementia.neurocraft.client;

import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

import static com.dementia.neurocraft.config.ServerConfigs.ITEMS_LOSE_LETTERS;
import static com.dementia.neurocraft.config.ServerConfigs.REPLACE_ITEMS_IN_INVENTORY;
import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientInventoryTroubles {
    private static int c = 1;

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
        if (tick.side == LogicalSide.CLIENT && tick.phase == TickEvent.Phase.END) {
            var player = Minecraft.getInstance().player;
            if (player == null)
                return;
            var inventory = Minecraft.getInstance().player.getInventory();
            var playerSanity = getPlayerSanityClient();
            if (c++ % 2 == 0 && new Random().nextInt(PEAK_SANITY.get()) < playerSanity) {
                if (!ITEMS_LOSE_LETTERS.get())
                    return;

                int slotIndex = getRandomNonEmptySlotIndex(inventory);
                if (slotIndex == -1)
                    return;
                ItemStack itemStack = inventory.getItem(slotIndex);
                var oldName = itemStack.getHoverName().getString();
                var newName = removeRandomChar(oldName);
                if (!newName.isEmpty() && !containsObfuscatedText(oldName)) {
                    itemStack.setHoverName(Component.literal(newName));
                } else {
                    itemStack.setHoverName(Component.literal(ChatFormatting.OBFUSCATED + createRandomLengthA()));
                }
            }
            if (c % 30 == 0 && new Random().nextInt(PEAK_SANITY.get()) < playerSanity) {
                if (!REPLACE_ITEMS_IN_INVENTORY.get())
                    return;

                int slotIndex = getRandomNonEmptySlotIndex(inventory);
                if (slotIndex == -1)
                    return;
                ItemStack itemStack = inventory.getItem(slotIndex);

                inventory.setItem(slotIndex, new ItemStack(getRandomItem(), itemStack.getCount()));
                c = 1;
            }
        }
    }

    public static int getRandomNonEmptySlotIndex(Inventory inventory) {
        ArrayList<Integer> nonEmptySlots = new ArrayList<>();

        for (int i = 0; i < inventory.getContainerSize(); i++) {
            // Check if the slot is not empty
            if (!inventory.getItem(i).isEmpty()) {
                nonEmptySlots.add(i);
            }
        }
        // select a random index from the non-empty slots
        return nonEmptySlots.isEmpty() ? -1:  nonEmptySlots.get(new Random().nextInt(nonEmptySlots.size()));
    }


    public static Item getRandomItem() {
        List<Field> itemFields = Stream.of(Items.class.getDeclaredFields())
                .filter(field -> Item.class.isAssignableFrom(field.getType()))
                .toList();

        while (true) {
            try {
                Field randomField = itemFields.get(new Random().nextInt(itemFields.size()));
                Item block = (Item) randomField.get(null);
                return block.asItem();
            } catch (IllegalAccessException ignored) {
            }
        }
    }

    public static String removeRandomChar(String s) {
        if (s == null || s.isEmpty()) {
            return s;
        }

        int randomIndex = new Random().nextInt(s.length());
        return s.substring(0, randomIndex) + s.substring(randomIndex + 1);
    }

    public static boolean containsObfuscatedText(String text) {
        return text.contains("§k");
    }

    public static String createRandomLengthA() {
        Random rand = new Random();
        int length = rand.nextInt(20) + 1; // Generate a random number between 1 and 20
        return "A".repeat(length); // Return the 'A' character repeated 'length' times
    }
}
