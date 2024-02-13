package com.dementia.neurocraft.server;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.network.CHallBlockListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.dementia.neurocraft.config.ServerConfigs.FAKE_BREAK_BLOCKS;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID)
public class BlockDestroyHallucinations {
    @SubscribeEvent
    public static void onBlockDestroyEvent(BlockEvent.BreakEvent event) {
        if (!FAKE_BREAK_BLOCKS.get())
            return;

        var player = (ServerPlayer) event.getPlayer();
        if (player == null)
            return;

        var playerSanity = getPlayerSanity(player);
        boolean replaceBlock = (new Random().nextInt((int) (PEAK_SANITY.get() * 1.5)) < playerSanity);
        if (replaceBlock) {
            var bp = event.getPos();
            event.setCanceled(true);
            ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(bp, Blocks.AIR.defaultBlockState());
            PacketHandler.sendVanillaPacket(packet, player, 2);
            PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(new int[]{bp.getX(), bp.getY(), bp.getZ()}), player);
        }
    }
}
