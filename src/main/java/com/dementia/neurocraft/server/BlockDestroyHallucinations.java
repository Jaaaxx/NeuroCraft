package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.network.CHallBlockListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = NeuroCraft.MODID)
public class BlockDestroyHallucinations {
    @SubscribeEvent
    public static void onBlockDestroyEvent(BlockEvent.BreakEvent event) {
        var player = (ServerPlayer) event.getPlayer();
        if (player == null)
            return;

        var playerSanity = getPlayerSanity(player);
        boolean replaceBlock = (new Random().nextInt(PEAK_SANITY) < playerSanity);
        if (replaceBlock) {
            var bp = event.getPos();
            event.setCanceled(true);
            ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(bp, Blocks.AIR.defaultBlockState());
            PacketHandler.sendVanillaPacket(packet, player, 2);
            PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(new int[]{bp.getX(), bp.getY(), bp.getZ()}), player);
        }
    }
}
