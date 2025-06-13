package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.FeatureBlockPlace;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.CHallBlockListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;

import java.util.concurrent.ThreadLocalRandom;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;
import static com.dementia.neurocraft.common.util.BlockUtils.getRandomBlock;

public final class FakeBlockPlacing extends FeatureBlockPlace {
    private BlockEvent.EntityPlaceEvent currentEvent;

    public FakeBlockPlacing() {
        super("FAKE_PLACE_BLOCKS", "Fake Block Placing", 400, 0.2, 0, false, FeatureTrigger.BLOCK_PLACE);
    }


    @Override
    public void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        this.currentEvent = event;
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (currentEvent == null || player == null) return;

        BlockPos pos = currentEvent.getPos();
        BlockState placed = currentEvent.getPlacedBlock();
        BlockState hallucinated = ThreadLocalRandom.current().nextBoolean() ? placed : getRandomBlock();

        if (hallucinated == placed) {
            currentEvent.getLevel().setBlock(pos, Blocks.AIR.defaultBlockState(), 1);
        }

        PacketHandler.sendVanillaPacket(new ClientboundBlockUpdatePacket(pos, hallucinated), player, 2);
        PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(new int[]{pos.getX(), pos.getY(), pos.getZ()}), player);

        HallucinationOccured(player, false, true);
        currentEvent = null;
    }

}
