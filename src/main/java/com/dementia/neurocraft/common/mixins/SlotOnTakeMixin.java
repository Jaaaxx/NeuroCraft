package com.dementia.neurocraft.common.mixins;

import com.dementia.neurocraft.server.internal.FurnaceSnapshot;
import com.dementia.neurocraft.server.internal.FurnaceTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Slot.class)
public abstract class SlotOnTakeMixin {

    @Inject(method = "onTake", at = @At("HEAD"))
    private void neurocraft$trackPlayerRemoval(Player player,
                                               ItemStack removed,
                                               CallbackInfo ci) {
        Slot self = (Slot) (Object) this;

        if (!(self.container instanceof AbstractFurnaceBlockEntity furnace)) return;
        int index = self.getContainerSlot();            // 0 = ingredient, 1 = fuel
        if (index != 0 && index != 1) return;

        BlockPos pos = furnace.getBlockPos();
        FurnaceSnapshot snap = FurnaceTracker.get(pos);
        if (snap == null) return;

        if (index == 0) snap.shrinkInput(removed.getCount());
        else snap.shrinkFuel(removed.getCount());
    }
}