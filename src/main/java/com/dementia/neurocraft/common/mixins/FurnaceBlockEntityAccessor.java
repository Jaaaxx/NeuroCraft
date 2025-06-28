package com.dementia.neurocraft.common.mixins;

import net.minecraft.world.level.block.entity.AbstractFurnaceBlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractFurnaceBlockEntity.class)
public interface FurnaceBlockEntityAccessor {
    @Accessor("litTime")
    void neurocraft$setLitTime(int time);

    @Accessor("litDuration")
    void neurocraft$setLitDuration(int duration);

    @Accessor("cookingProgress")
    int neurocraft$getCookingProgress();
}
