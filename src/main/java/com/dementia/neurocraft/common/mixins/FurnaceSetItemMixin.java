package com.dementia.neurocraft.common.mixins;

import com.dementia.neurocraft.server.internal.FurnaceTracker;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AbstractFurnaceBlockEntity.class)
public abstract class FurnaceSetItemMixin {

    @Inject(method = "setItem", at = @At("TAIL"))
    private void neurocraft$trackFirstFill(int slot, ItemStack stack, CallbackInfo ci) {
        if (slot != 0 && slot != 1) return;

        AbstractFurnaceBlockEntity self = (AbstractFurnaceBlockEntity) (Object) this;
        BlockPos pos = self.getBlockPos();

        // after the assignment, so getItem() reflects the new state
        FurnaceTracker.merge(pos, self.getItem(0).copy(), self.getItem(1).copy());
    }
}
