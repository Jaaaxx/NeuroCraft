package com.dementia.neurocraft.client.internal;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Items;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;

public final class InventoryUtils {
    private static final Random RNG = new Random();

    public static int getRandomNonEmptySlotIndex(Inventory inventory) {
        ArrayList<Integer> nonEmptySlots = new ArrayList<>();
        for (int i = 0; i < inventory.getContainerSize(); i++) {
            if (!inventory.getItem(i).isEmpty()) nonEmptySlots.add(i);
        }
        return nonEmptySlots.isEmpty() ? -1 : nonEmptySlots.get(RNG.nextInt(nonEmptySlots.size()));
    }

    public static Item getRandomItem() {
        List<Field> itemFields = Stream.of(Items.class.getDeclaredFields())
                .filter(field -> Item.class.isAssignableFrom(field.getType()))
                .toList();

        while (true) {
            try {
                Field field = itemFields.get(RNG.nextInt(itemFields.size()));
                return ((Item) field.get(null)).asItem();
            } catch (IllegalAccessException ignored) {}
        }
    }

    public static String removeRandomChar(String s) {
        if (s == null || s.isEmpty()) return s;
        int i = RNG.nextInt(s.length());
        return s.substring(0, i) + s.substring(i + 1);
    }

    public static boolean containsObfuscatedText(String s) {
        return s.contains("§k");
    }

    public static String createRandomLengthA() {
        return "A".repeat(RNG.nextInt(20) + 1);
    }
}
