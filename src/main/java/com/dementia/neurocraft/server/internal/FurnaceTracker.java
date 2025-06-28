package com.dementia.neurocraft.server.internal;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class FurnaceTracker {
    private static final Map<BlockPos, FurnaceSnapshot> DATA = new HashMap<>();

    public static void merge(BlockPos pos, ItemStack in, ItemStack fu) {
        FurnaceSnapshot snap = DATA.computeIfAbsent(pos, p -> new FurnaceSnapshot(null));
        if (!in.isEmpty()) snap.offerInput(in);
        if (!fu.isEmpty()) snap.offerFuel(fu);
    }

    public static void merge(BlockPos pos, ItemStack in, ItemStack fu, UUID owner) {
        FurnaceSnapshot snap = DATA.computeIfAbsent(pos, p -> new FurnaceSnapshot(owner));
        if (snap.owner() == null) snap.setOwner(owner);
        if (!in.isEmpty()) snap.offerInput(in);
        if (!fu.isEmpty()) snap.offerFuel(fu);
    }

    public static FurnaceSnapshot get(BlockPos pos) {
        return DATA.get(pos);
    }

    public static void clear(BlockPos pos) {
        DATA.remove(pos);
    }

    public static boolean has(BlockPos pos) {
        return DATA.containsKey(pos);
    }
}
