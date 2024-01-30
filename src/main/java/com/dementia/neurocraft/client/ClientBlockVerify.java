package com.dementia.neurocraft.client;

import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SRefreshClientBlockList;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.Event;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import static com.dementia.neurocraft.NeuroCraft.*;
import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;
import static com.dementia.neurocraft.network.SRefreshClientBlockList.toIntArray;

@Mod.EventBusSubscriber(modid = MODID)
public class ClientBlockVerify {
    public static Level hallucinationBlockLevel;
    public static List<BlockPos> hallucinationBlocks = new ArrayList<>();
    private static int tickC = 1;

    @SubscribeEvent
    public static void onClientBreakBlockEvent(PlayerInteractEvent.LeftClickBlock event) {
        if (event.getAction() == PlayerInteractEvent.LeftClickBlock.Action.STOP) {
            var pos = event.getPos();
            if (hallucinationBlocks.contains(pos)) {
                removeHallucinationBlocks(pos, event.getEntity());
            }
        }
    }

    @SubscribeEvent
    public static void onClientPlaceBlockEvent(BlockEvent.EntityPlaceEvent event) {
        Player player = (Player) event.getEntity();
        if (player == null)
            return;
        var pos = getPlayerPOVHitResult((Level) event.getLevel(), player, net.minecraft.world.level.ClipContext.Fluid.NONE).getBlockPos();
        if (hallucinationBlocks.contains(pos)) {
            removeHallucinationBlocks(pos, (Player) event.getEntity());
            event.setCanceled(true);
        }
    }

    public static @NotNull Vec3 clipWithDistance(@NotNull Player player, @NotNull Level level, double clipDistance) {
        double vecX = Math.sin(-player.getYRot() * (Math.PI / 180.0) - Math.PI) * -Math.cos(-player.getXRot() * (Math.PI / 180.0));
        double vecY = Math.sin(-player.getXRot() * (Math.PI / 180.0));
        double vecZ = Math.cos(-player.getYRot() * (Math.PI / 180.0) - Math.PI) * -Math.cos(-player.getXRot() * (Math.PI / 180.0));
        return level.clip(new ClipContext(player.getEyePosition(), player.getEyePosition().add(vecX * clipDistance, vecY * clipDistance, vecZ * clipDistance), ClipContext.Block.OUTLINE, ClipContext.Fluid.NONE, player)).getLocation();
    }

    public static void removeHallucinationBlocks(BlockPos pos, Player player) {
        var item = player.level().getBlockState(pos).getBlock().asItem();
        hallucinationBlocks.remove(pos);
        PacketHandler.sendToServer(new SRefreshClientBlockList(toIntArray(pos), Item.getId(item)));
        HallucinationOccuredClient();
    }


    public static void removeHallucinationBlocks(BlockPos pos, Player player, Iterator<BlockPos> ite) {
        var item = player.level().getBlockState(pos).getBlock().asItem();
        ite.remove();
        PacketHandler.sendToServer(new SRefreshClientBlockList(toIntArray(pos), Item.getId(item)));
        HallucinationOccuredClient();
    }

    @SubscribeEvent
    public static void onClientTickEvent(TickEvent.PlayerTickEvent event) {
        if (event.side != LogicalSide.CLIENT)
            return;

        var player = event.player;

        if (tickC++ == 5) {
            var onPos = player.getOnPos();
            for (Iterator<BlockPos> iterator = hallucinationBlocks.iterator(); iterator.hasNext();) {
                var block = iterator.next();
                if (player.level().getBlockState(block).getBlock() == Blocks.AIR) {
                    int dx = Math.abs(onPos.getX() - block.getX());
                    int dy = Math.abs(onPos.getY() - block.getY());
                    int dz = Math.abs(onPos.getZ() - block.getZ());

                    if (dx <= 1 && dy <= 3 && dz <= 1) {
                        removeHallucinationBlocks(block, player, iterator);
                    }

                } else {
                    if (block == onPos) {
                        removeHallucinationBlocks(block, player, iterator);
                    }
                }
            }
            tickC = 0;
        }
    }


    protected static BlockHitResult getPlayerPOVHitResult(Level level, Player player, ClipContext.Fluid context) {
        float f = player.getXRot();
        float f1 = player.getYRot();
        Vec3 vec3 = player.getEyePosition();
        float f2 = Mth.cos(-f1 * 0.017453292F - 3.1415927F);
        float f3 = Mth.sin(-f1 * 0.017453292F - 3.1415927F);
        float f4 = -Mth.cos(-f * 0.017453292F);
        float f5 = Mth.sin(-f * 0.017453292F);
        float f6 = f3 * f4;
        float f7 = f2 * f4;
        double d0 = player.getBlockReach();
        Vec3 vec31 = vec3.add((double) f6 * d0, (double) f5 * d0, (double) f7 * d0);
        return level.clip(new ClipContext(vec3, vec31, net.minecraft.world.level.ClipContext.Block.OUTLINE, context, player));
    }


//
//    @SubscribeEvent
//    public static void onEvent(Event e) {
//        try {
//            var x = Minecraft.getInstance();
//            if (x.player == null) {
//                return;
//            }
//            LOGGER.info("Event: " + e.toString());
//        } catch (Exception ignored) {
//        }
//    }

}
