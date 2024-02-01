package com.dementia.neurocraft.client;

import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SForceBlockUpdatePacket;
import com.dementia.neurocraft.server.BlockPlaceHallucinations;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import static com.dementia.neurocraft.NeuroCraft.*;
import static com.dementia.neurocraft.network.SRefreshClientBlockList.toIntArray;

@Mod.EventBusSubscriber(modid = MODID)
public class RandomizeTextures {
    public static boolean crazyRenderingActive = false;
    private static boolean prevCRA = false;
    private static int c = 0;
    private static final ArrayList<BlockPos> changedBlocks = new ArrayList<>();

    @SubscribeEvent
    public static void onClientRenderEvent(RenderLevelStageEvent event) {
        if (c++ % 800 == 0 || prevCRA != crazyRenderingActive) {
            if (prevCRA != crazyRenderingActive)
                prevCRA = crazyRenderingActive;
            var instance = Minecraft.getInstance();
            var player = instance.player;
            if (player == null)
                return;
            var level = player.level();
            if (crazyRenderingActive) {
                var nearbyBlocks = getBlocksInRadius(player.level(), player.getOnPos(), 5);
                nearbyBlocks.forEach((pos, state) -> {
                    var block = state.getBlock();
                    if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
                        player.clientLevel.setBlock(pos, BlockPlaceHallucinations.getRandomBlock(), 1);
                        changedBlocks.add(pos);
                    }
                });
            }

            if (!crazyRenderingActive && !changedBlocks.isEmpty()) {
                for (var pos : changedBlocks) {
                    PacketHandler.sendToServer(new SForceBlockUpdatePacket(toIntArray(pos)));
                }
                changedBlocks.clear();
            }

            c = 1;
        }
    }


    public static HashMap<BlockPos, BlockState> getBlocksInRadius(Level level, BlockPos center, int radius) {
        HashMap<BlockPos, BlockState> blocks = new HashMap<>();

        for (int x = -radius; x <= radius; x++) {
            for (int y = -2; y <= radius; y++) {
                for (int z = -radius; z <= radius; z++) {
                    BlockPos pos = center.offset(x, y, z);
                    BlockState state = level.getBlockState(pos);
                    blocks.put(pos, state);
                }
            }
        }

        return blocks;
    }
}