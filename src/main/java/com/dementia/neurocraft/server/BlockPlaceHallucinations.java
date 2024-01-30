package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.network.CHallBlockListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetContentPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
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
public class BlockPlaceHallucinations {
    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player))
            return;
        // AFFECTS SELF
        ConfuseBlocks(event, player);

        // AFFECTS NEAREST PLAYER
        ServerPlayer nearestPlayer = (ServerPlayer) event.getLevel().getNearestPlayer(TargetingConditions.DEFAULT.selector((e) -> e !=  player), player);
        if (nearestPlayer == null)
            return;
        ConfuseBlocks(event, nearestPlayer);
    }

    private static void ConfuseBlocks(BlockEvent.EntityPlaceEvent event, ServerPlayer player) {
        long playerSanity = getPlayerSanity(player);
        boolean replaceBlock = (new Random().nextInt(PEAK_SANITY) < playerSanity);
        if (replaceBlock) {
            var bp = event.getPos();
            ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(bp, event.getPlacedBlock());
            event.getLevel().setBlock(bp, Blocks.AIR.defaultBlockState(), 1);
            PacketHandler.sendVanillaPacket(packet, player, 2);
            PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(new int[]{bp.getX(), bp.getY(), bp.getZ()}), player);
        } else {
            replaceBlock = (new Random().nextInt(PEAK_SANITY) < playerSanity);
            if (replaceBlock) {
                var bp = event.getPos();
                ClientboundBlockUpdatePacket packet = new ClientboundBlockUpdatePacket(bp, getRandomBlock());
                PacketHandler.sendVanillaPacket(packet, player, 2);
                PacketHandler.sendToPlayer(new CHallBlockListUpdatePacket(new int[]{bp.getX(), bp.getY(), bp.getZ()}), player);
            }
        }
    }

    public static BlockState getRandomBlock() {
        List<Field> blockFields = Stream.of(Blocks.class.getDeclaredFields())
                .filter(field -> Block.class.isAssignableFrom(field.getType()))
                .toList();

        while (true) {
            try {
                Field randomField = blockFields.get(new Random().nextInt(blockFields.size()));
                Block block = (Block) randomField.get(null);
                return block.defaultBlockState();
            } catch (IllegalAccessException ignored) {}
        }
    }
}
