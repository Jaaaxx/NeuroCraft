package com.dementia.neurocraft.server;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.network.CHallBlockListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dementia.neurocraft.config.ServerConfigs.FAKE_PLACE_BLOCKS;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID)
public final class BlockPlaceHallucinations {
    private static final List<BlockState> OPAQUE_BLOCKS = Stream.of(Blocks.class.getDeclaredFields())
            .filter(f -> Block.class.isAssignableFrom(f.getType()))
            .map(BlockPlaceHallucinations::toBlock)
            .filter(Objects::nonNull)
            .map(Block::defaultBlockState)
//            .filter(BlockState::canOcclude)                 // solid, non-transparent only
            .collect(Collectors.toUnmodifiableList());

    private static Block toBlock(Field f) {
        try { f.setAccessible(true); return (Block) f.get(null); }
        catch (ReflectiveOperationException ignored) { return null; }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent evt) {
        if (!FAKE_PLACE_BLOCKS.get() || !(evt.getEntity() instanceof ServerPlayer placer)) return;

        hallucinate(evt, placer);

        ServerPlayer other = (ServerPlayer) evt.getLevel()
                .getNearestPlayer(TargetingConditions.DEFAULT, placer);
        if (other != null) hallucinate(evt, other);
    }

    private static void hallucinate(BlockEvent.EntityPlaceEvent evt, ServerPlayer target) {
        long sanity   = getPlayerSanity(target);
        boolean swap  = ThreadLocalRandom.current().nextInt(PEAK_SANITY.get()) < sanity;

        if (!swap) return;

        var pos = evt.getPos();
        BlockState visual = (ThreadLocalRandom.current().nextBoolean())
                ? evt.getPlacedBlock()
                : getRandomBlock();

        ClientboundBlockUpdatePacket vanilla = new ClientboundBlockUpdatePacket(pos, visual);

        if (visual == evt.getPlacedBlock())
            evt.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 1);

        PacketHandler.sendVanillaPacket(vanilla, target, 2);
        PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(
                new int[] { pos.getX(), pos.getY(), pos.getZ() }), target);
    }

    public static BlockState getRandomBlock() {
        return OPAQUE_BLOCKS.get(ThreadLocalRandom.current().nextInt(OPAQUE_BLOCKS.size()));
    }
}
