package com.dementia.neurocraft.common.util;

import com.dementia.neurocraft.server.features.impl.FakeBlockPlacing;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Stream;

public class BlockUtils {
    private static final List<BlockState> ALL_BLOCKS = Stream.of(Blocks.class.getDeclaredFields())
            .filter(f -> Block.class.isAssignableFrom(f.getType()))
            .map(BlockUtils::toBlock)
            .filter(Objects::nonNull)
            .map(Block::defaultBlockState)
            .toList();


    private static Block toBlock(Field f) {
        try {
            f.setAccessible(true);
            return (Block) f.get(null);
        } catch (ReflectiveOperationException ignored) {
            return null;
        }
    }

    public static BlockState getRandomBlock() {
        return ALL_BLOCKS.get(ThreadLocalRandom.current().nextInt(ALL_BLOCKS.size()));
    }
}
