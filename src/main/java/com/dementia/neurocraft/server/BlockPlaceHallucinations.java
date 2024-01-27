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
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = NeuroCraft.MODID)
public class BlockPlaceHallucinations {
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        var player = (ServerPlayer) event.getEntity();
        if (player == null)
            return;
        var playerSanity = getPlayerSanity(player);
        boolean replaceBlock = (new Random().nextInt(PEAK_SANITY) < playerSanity);
        if (replaceBlock) {
            event.setCanceled(true);
            var bp = event.getPos();
            ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(bp, event.getPlacedBlock());
            PacketHandler.sendVanillaPacket(packet, player, 30);
            PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(new int[]{bp.getX(), bp.getY(), bp.getZ()}), player);
            // refresh player's inventory to match server
            PacketHandler.sendVanillaPacket(new ClientboundContainerSetSlotPacket(-1, -1, -1, player.containerMenu.getCarried()), player);
        }
    }
}