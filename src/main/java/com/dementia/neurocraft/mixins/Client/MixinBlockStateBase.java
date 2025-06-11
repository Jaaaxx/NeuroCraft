package com.dementia.neurocraft.mixins.Client;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static com.dementia.neurocraft.client.RandomizeTextures.*;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class MixinBlockStateBase {
    @Unique
    private static boolean neuroCraft$defaultRendering() {
//        return schizoRenderType == SchizoRenderType.GRAYSCALE || schizoRenderType == SchizoRenderType.PURE_BLACK;
        return false;
    }

    @Inject(at = @At("HEAD"), method = "getCollisionShape(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/phys/shapes/CollisionContext;)Lnet/minecraft/world/phys/shapes/VoxelShape;", cancellable = true)
    protected void getCollisionShape(BlockGetter blockGetter, BlockPos blockPos, CollisionContext context, CallbackInfoReturnable<VoxelShape> callback) {
        if (crazyRenderingActive && !neuroCraft$defaultRendering()) {
            if (changedBlocks.containsKey(blockPos)) {
                callback.setReturnValue(Shapes.block());
            }
            if (changedLiquids.containsKey(blockPos)) {
                callback.setReturnValue(Shapes.empty());
            }
        }
    }

    @Inject(at = @At("HEAD"), method = "isSuffocating(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", cancellable = true)
    protected void isSuffocating(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callback) {
        if (crazyRenderingActive && !neuroCraft$defaultRendering()) {
            if (changedLiquids.containsKey(blockPos)) {
                callback.setReturnValue(false);
            }
        }
    }


    @Inject(at = @At("HEAD"), method = "isViewBlocking(Lnet/minecraft/world/level/BlockGetter;Lnet/minecraft/core/BlockPos;)Z", cancellable = true)
    protected void isViewBlocking(BlockGetter blockGetter, BlockPos blockPos, CallbackInfoReturnable<Boolean> callback) {
        if (crazyRenderingActive && !neuroCraft$defaultRendering()) {
            if (changedLiquids.containsKey(blockPos)) {
                callback.setReturnValue(false);
            }
        }
    }
}
