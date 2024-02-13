package com.dementia.neurocraft.client;

import com.dementia.neurocraft.common.ClientSoundManager;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SForceBlockUpdatePacket;
import com.dementia.neurocraft.server.BlockPlaceHallucinations;
import com.dementia.neurocraft.util.ModBlocksRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;

import static com.dementia.neurocraft.NeuroCraft.MODID;
import static com.dementia.neurocraft.client.ClientOptionsChanges.currentSchitzoMusic;
import static com.dementia.neurocraft.network.SRefreshClientBlockList.toIntArray;
import static com.dementia.neurocraft.util.ModSoundEventsRegistry.STATICSWITCH;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class RandomizeTextures {
    public static boolean crazyRenderingActive = false;
    private static boolean prevCRA = false;
    private static boolean typeWASNORENDER = false;
    private static int c = 0;
    public static final HashSet<BlockPos> changedBlocks = new HashSet<>();
    public static final HashSet<BlockPos> changedLiquids = new HashSet<>();

    @SubscribeEvent
    public static void onClientRenderEvent(RenderLevelStageEvent event) {
        if (c++ % 400 == 0 || prevCRA != crazyRenderingActive) {
            if (prevCRA != crazyRenderingActive)
                prevCRA = crazyRenderingActive;
            var instance = Minecraft.getInstance();
            var player = instance.player;
            if (player == null)
                return;
            var level = player.level();
            if (crazyRenderingActive) {
                var nearbyBlocks = getBlocksInRadius(level, player.getOnPos(), 5);
                if (new Random().nextBoolean()) {
                    // removes all block textures
                    nearbyBlocks.forEach((pos, state) -> {
                        var block = state.getBlock();
                        if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
                            var v1 = ModBlocksRegistry.SMOOTH_BLOCK.get();
                            player.clientLevel.setBlock(pos, v1.defaultBlockState(), 1);
                            if (block instanceof LiquidBlock || block instanceof IFluidBlock) {
                                changedLiquids.add(pos);
                            } else {
                                changedBlocks.add(pos);
                            }
                        }
                    });
                    if (!typeWASNORENDER) {
                        ClientSoundManager.playSoundRandomPitchVolume(STATICSWITCH.get());
                    }
                    typeWASNORENDER = true;
                } else {
                    // randomizes all block textures
                    nearbyBlocks.forEach((pos, state) -> {
                        var block = state.getBlock();
                        if (block != Blocks.AIR && block != Blocks.CAVE_AIR && block != Blocks.VOID_AIR) {
                            var v1 = BlockPlaceHallucinations.getRandomBlock();
                            player.clientLevel.setBlock(pos, v1, 1);
                            if (block instanceof LiquidBlock || block instanceof IFluidBlock) {
                                changedLiquids.add(pos);
                            } else {
                                changedBlocks.add(pos);
                            }
                        }
                    });
                    if (typeWASNORENDER) {
                        ClientSoundManager.playSound(STATICSWITCH.get(), 0.25f, 1);
                    }
                    typeWASNORENDER = false;
                }
            }
            if (!crazyRenderingActive) {
                if (!changedBlocks.isEmpty()) {
                    for (var pos : changedBlocks) {
                        PacketHandler.sendToServer(new SForceBlockUpdatePacket(toIntArray(pos)));
                    }
                    changedBlocks.clear();
                }
                if (!changedLiquids.isEmpty()) {
                    for (var pos : changedLiquids) {
                        PacketHandler.sendToServer(new SForceBlockUpdatePacket(toIntArray(pos)));
                    }
                    changedBlocks.clear();
                }
            }

            c = 1;
        }
    }


    @SubscribeEvent
    public static void onPlayerDeathEvent(LivingDeathEvent event) {
        if (crazyRenderingActive && event.getEntity() instanceof ServerPlayer) {
            var instance = Minecraft.getInstance();
            var player = instance.player;
            if (player == null)
                return;
            if (event.getEntity().getName().equals(player.getName())) {
                crazyRenderingActive = false;
                if (currentSchitzoMusic != null)
                    ClientSoundManager.stopSound(currentSchitzoMusic);
            }
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