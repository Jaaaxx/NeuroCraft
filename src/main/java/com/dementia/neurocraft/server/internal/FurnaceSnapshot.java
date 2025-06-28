package com.dementia.neurocraft.server.internal;

import net.minecraft.world.item.ItemStack;

import java.util.UUID;

public final class FurnaceSnapshot {
    private UUID owner;
    private ItemStack input = ItemStack.EMPTY;
    private ItemStack fuel  = ItemStack.EMPTY;

    public FurnaceSnapshot(UUID owner) {
        this.owner = owner;
    }
    public void setOwner(UUID id) { this.owner = id; }
    public UUID owner() { return owner; }

    public void offerInput(ItemStack stack) {
        if (this.input.isEmpty()) this.input = stack.copy();
    }

    public void offerFuel(ItemStack stack) {
        if (this.fuel.isEmpty()) this.fuel = stack.copy();
    }

    public ItemStack input() { return input; }
    public ItemStack fuel()  { return fuel; }

    public void shrinkInput(int amount) {
        if (!input.isEmpty()) input.shrink(amount);
    }

    public void shrinkFuel(int amount) {
        if (!fuel.isEmpty()) fuel.shrink(amount);
    }
}
