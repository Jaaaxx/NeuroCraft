package com.dementia.neurocraft.mixins;

import net.minecraft.core.BlockPos;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockBehaviour.class)
public class MixinBlockStateBase {
    //m_6097_()Z
    @Inject(at = @At("HEAD"), method = "m_60812_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
    private void getCollisionShape(BlockGetter p_60813_, BlockPos p_60814_, CallbackInfoReturnable<VoxelShape> callback) {
        System.out.println("fuck1");
        callback.setReturnValue(Shapes.block());
    }

    @Inject(at = @At("HEAD"), method = "m_60742_(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
    private void getCollisionShape(BlockGetter p_60813_, BlockPos p_60814_, CollisionContext p_60745_, CallbackInfoReturnable<VoxelShape> callback) {
        System.out.println("fuck2");
        callback.setReturnValue(Shapes.block());
    }
}
