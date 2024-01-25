package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.spongepowered.asm.util.Constants;

import java.util.*;
import java.util.stream.Collectors;

import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = NeuroCraft.MODID)
public class OreHallucinations {

//    private static final List<Block> ores = new ArrayList<>(List.of(
//            Blocks.IRON_ORE, Blocks.DEEPSLATE_IRON_ORE,
//            Blocks.DIAMOND_ORE, Blocks.DEEPSLATE_DIAMOND_ORE,
//            Blocks.COAL_ORE, Blocks.DEEPSLATE_COAL_ORE));

    @SubscribeEvent
    public static void onBlockBreakEvent(BlockEvent.BreakEvent event) {
        var state = event.getState();
        var player = event.getPlayer();
        event.getPos();
        if (state.getBlock().getName().toString().contains("ore")) {
            var playerSanity = getPlayerSanity(player);
            boolean replaceBlock = (new Random().nextInt(PEAK_SANITY) < playerSanity);
            var shadowBlocks = new Random().nextBoolean();
            replaceBlock = true;
            if (replaceBlock) {
                event.setCanceled(true);
                var veinBlocks = getAllBlocksInVein(state, event.getPos(), event.getLevel());
                if (shadowBlocks) {
                    for (var blockPos : veinBlocks) {
                        if (state.getBlock().getName().toString().contains("deepslate")) {
                            event.getLevel().setBlock(blockPos, Blocks.DEEPSLATE.defaultBlockState(), 1);
                        } else {
                            event.getLevel().setBlock(blockPos, Blocks.STONE.defaultBlockState(), 1);
                        }
                    }
                } else {
                    for (var blockPos : veinBlocks) {
                        if (state.getBlock().getName().toString().contains("deepslate")) {
                            event.getLevel().setBlock(blockPos, Blocks.DEEPSLATE.defaultBlockState(), 2);
                        } else {
                            event.getLevel().setBlock(blockPos, Blocks.STONE.defaultBlockState(), 2);
                        }
                    }
                }
                player.setPos(player.getPosition(0));
                HallucinationOccured(player);
            }
        }
    }


    private static List<BlockPos> getSurroundingBlocks(LevelAccessor level, BlockPos centerPos, int radius) {
        List<BlockPos> surroundingBlocks = new ArrayList<>();
        for (int x = -radius; x <= radius; x++) {
            for (int y = -radius; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos targetPos = centerPos.offset(x, y, z);
                    surroundingBlocks.add(targetPos);
                }
            }
        }
        return surroundingBlocks;
    }

    private static ArrayList<BlockPos> getAllBlocksInVein(BlockState state, BlockPos pos, LevelAccessor level) {
        // returns all blocks of vein using nearby block iteration
        ArrayList<BlockPos> blocksInVein = new ArrayList<>();
        blocksInVein.add(pos);

        ArrayList<BlockPos> checkedBlocks = new ArrayList<>();
        checkedBlocks.add(pos);

        List<BlockPos> positions = new LinkedList<>();
        positions.add(pos);


        while (!positions.isEmpty()) {
            BlockPos currentPos = positions.remove(0);
            getSurroundingBlocks(level, currentPos, 1).forEach(blockPos -> {
                var blockState = level.getBlockState(blockPos);
                if (!checkedBlocks.contains(blockPos)) {
                    checkedBlocks.add(blockPos);
                    if (blockState.getBlock() == state.getBlock()) {
                        blocksInVein.add(blockPos);
                        positions.add(blockPos);
                    }
                }
            });
        }
        return blocksInVein;
    }
}