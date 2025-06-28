package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.FeatureBlockBreak;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.CHallBlockListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;

//todo fix
public final class FakeBlockBreaking extends FeatureBlockBreak {

    private BlockPos lastTriggeredPos;

    public FakeBlockBreaking() {
        super("FAKE_BREAK_BLOCKS", "Fake Block Breaking", 400, 0.25, 0, false, FeatureTrigger.SERVER_BLOCK_BREAK, false);
    }

    @Override
    public void onBlockBreak(BlockEvent.BreakEvent event) {
        this.lastTriggeredPos = event.getPos();
    }

    @Override
    public void performServer(ServerPlayer player) {
        if (lastTriggeredPos == null) return;

        PacketHandler.sendVanillaPacket(new ClientboundBlockUpdatePacket(lastTriggeredPos, Blocks.AIR.defaultBlockState()), player, 2);

        PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(new int[]{lastTriggeredPos.getX(), lastTriggeredPos.getY(), lastTriggeredPos.getZ()}), player);

        HallucinationOccured(player, false, true);
        lastTriggeredPos = null;
    }
}
