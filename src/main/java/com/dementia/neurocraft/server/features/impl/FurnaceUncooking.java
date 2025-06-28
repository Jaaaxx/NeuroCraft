package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.mixins.AbstractFurnaceMenuAccessor;
import com.dementia.neurocraft.common.mixins.FurnaceBlockEntityAccessor;
import com.dementia.neurocraft.common.features.FeatureContainerOpen;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.server.internal.FurnaceSnapshot;
import com.dementia.neurocraft.server.internal.FurnaceTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.inventory.AbstractFurnaceMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AbstractFurnaceBlock;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;

@Mod.EventBusSubscriber(modid = MODID)
public final class FurnaceUncooking extends FeatureContainerOpen {
    private PlayerContainerEvent.Open event;

    public FurnaceUncooking() {
        super("FURNACE_UNCOOKING", "Furnace Uncooking", 300, 1, 0, true, FeatureTrigger.SERVER_CONTAINER_OPEN, false);
    }

    @SubscribeEvent
    public static void onFurnaceClosed(PlayerContainerEvent.Close e) {
        if (!(e.getContainer() instanceof AbstractFurnaceMenu menu)) return;

        Container c = ((AbstractFurnaceMenuAccessor) menu).neurocraft$getContainer();
        if (!(c instanceof AbstractFurnaceBlockEntity be)) return;

        BlockPos pos = be.getBlockPos();
        FurnaceTracker.merge(pos, be.getItem(0), be.getItem(1), e.getEntity().getUUID());
    }

    @Override
    public void onContainerOpen(PlayerContainerEvent.Open event) {
        this.event = event;
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (event == null) return;
        if (!(event.getContainer() instanceof AbstractFurnaceMenu furnaceMenu)) return;

        Container container = ((AbstractFurnaceMenuAccessor) furnaceMenu).neurocraft$getContainer();
        if (!(container instanceof AbstractFurnaceBlockEntity furnace)) return;

        BlockPos pos = furnace.getBlockPos();

        // Skip unless there is cooked output still in the result slot
        ItemStack result = furnace.getItem(2);
        if (result.isEmpty()) return;

        FurnaceSnapshot snap = FurnaceTracker.get(pos);
        if (snap == null || !snap.owner().equals(player.getUUID())) return;

        // Refund ingredients
        ItemStack ingNow = furnace.getItem(0);
        int refundI = snap.input().getCount() - ingNow.getCount();

        if (!ingNow.isEmpty()) {
            player.getInventory().add(ingNow.copy());
            furnace.setItem(0, ItemStack.EMPTY);
        }
        if (refundI > 0) {
            player.getInventory().add(new ItemStack(snap.input().getItem(), refundI));
        }
        ItemStack fuelNow = furnace.getItem(1);
        int refundF = snap.fuel().getCount() - fuelNow.getCount();
        if (!fuelNow.isEmpty()) {
            player.getInventory().add(fuelNow.copy());
            furnace.setItem(1, ItemStack.EMPTY);
        }
        if (refundF > 0) {
            player.getInventory().add(new ItemStack(snap.fuel().getItem(), refundF));
        }
        furnace.setItem(2, ItemStack.EMPTY);

        // Extinguish furnace
        ((FurnaceBlockEntityAccessor) furnace).neurocraft$setLitTime(0);
        ((FurnaceBlockEntityAccessor) furnace).neurocraft$setLitDuration(0);
        furnace.setChanged();

        BlockState state = furnace.getBlockState();
        if (state.hasProperty(AbstractFurnaceBlock.LIT) && state.getValue(AbstractFurnaceBlock.LIT)) {
            furnace.getLevel().setBlock(pos, state.setValue(AbstractFurnaceBlock.LIT, false), 3);
        }

        furnace.getLevel().sendBlockUpdated(pos, furnace.getBlockState(), furnace.getBlockState(), 3);

        FurnaceTracker.clear(pos);
        HallucinationOccured(player);
        this.event = null;
    }
}
