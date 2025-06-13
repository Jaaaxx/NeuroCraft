package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.FeatureBlockBreak;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

import java.util.*;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;


public final class OreHallucinationVein extends FeatureBlockBreak {

    private BlockState triggeredState;
    private BlockPos triggeredPos;
    private LevelAccessor level;

    public OreHallucinationVein() {
        super("ORE_HALLUCINATIONS", "Ore Vein Hallucinations",
                300, 0.3, 0, true, FeatureTrigger.BLOCK_BREAK);
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        var state = event.getState();
        if (!state.getBlock().getName().toString().contains("ore")) return;

        this.triggeredState = state;
        this.triggeredPos = event.getPos();
        this.level = event.getLevel();
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (triggeredState == null || triggeredPos == null || level == null) return;

        List<BlockPos> veinBlocks = getAllBlocksInVein(triggeredState, triggeredPos, level);
        boolean shadowBlocks = RNG.nextBoolean();

        for (BlockPos blockPos : veinBlocks) {
            BlockState newState = triggeredState.getBlock().getName().toString().contains("deepslate")
                    ? Blocks.DEEPSLATE.defaultBlockState()
                    : Blocks.STONE.defaultBlockState();
            level.setBlock(blockPos, newState, shadowBlocks ? 1 : 2);
        }

        player.setPos(player.getPosition(0));
        HallucinationOccured(player);

        triggeredState = null;
        triggeredPos = null;
        level = null;
    }

    private List<BlockPos> getAllBlocksInVein(BlockState match, BlockPos origin, LevelAccessor level) {
        List<BlockPos> found = new ArrayList<>();
        Queue<BlockPos> frontier = new LinkedList<>();
        Set<BlockPos> visited = new HashSet<>();

        frontier.add(origin);
        visited.add(origin);

        while (!frontier.isEmpty()) {
            BlockPos current = frontier.poll();
            found.add(current);

            for (BlockPos neighbor : getSurroundingBlocks(current)) {
                if (visited.contains(neighbor)) continue;
                if (level.getBlockState(neighbor).getBlock() == match.getBlock()) {
                    frontier.add(neighbor);
                }
                visited.add(neighbor);
            }
        }

        return found;
    }

    private List<BlockPos> getSurroundingBlocks(BlockPos center) {
        List<BlockPos> list = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                for (int z = -1; z <= 1; z++) {
                    list.add(center.offset(x, y, z));
                }
            }
        }
        return list;
    }
}
