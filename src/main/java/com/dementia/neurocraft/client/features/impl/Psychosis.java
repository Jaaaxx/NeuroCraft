package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.client.internal.ClientPlayerDeathEvent;
import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.internal.SoundManager;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.common.util.BlockUtils;
import com.dementia.neurocraft.util.ModBlocksRegistry;
import com.dementia.neurocraft.util.ModSoundEventsRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPauseEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;
import org.lwjgl.opengl.GL11;

import java.util.*;
import java.util.concurrent.*;

import static com.dementia.neurocraft.Neurocraft.LOGGER;
import static com.dementia.neurocraft.client.internal.EntityRandomizer.renderAllEntitiesAsRandomPlayers;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;
import static com.dementia.neurocraft.util.ModSoundEventsRegistry.STATICSWITCH;
import static com.dementia.neurocraft.util.ModSoundEventsRegistry.schizoMusicOptions;

@Mod.EventBusSubscriber(modid = com.dementia.neurocraft.Neurocraft.MODID, value = Dist.CLIENT)
public final class Psychosis extends Feature {

    private enum SchizoRenderType {ALL_BLOCKS, MISSING_TEXTURE}

    private enum PsychosisType {NORMAL_PSYCHOSIS, ROTATIONAL_PSYCHOSIS}

    public static volatile boolean active = false;

    public static final int RENDER_RADIUS = 12;
    public static final int Y_RENDER_RADIUS = 12;

    private static final BlockPos[] REL_OFFSETS;
    private static boolean screenShouldShake = false;

    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    static ScheduledFuture<?> disablePsychosisTask = null;

    static {
        List<BlockPos> tmp = new ArrayList<>((RENDER_RADIUS * 2 + 1) * (RENDER_RADIUS * 2 + 1) * (RENDER_RADIUS * 2 + 1));
        for (int x = -RENDER_RADIUS; x <= RENDER_RADIUS; ++x)
            for (int y = -Y_RENDER_RADIUS; y <= Y_RENDER_RADIUS; ++y)
                for (int z = -RENDER_RADIUS; z <= RENDER_RADIUS; ++z)
                    tmp.add(new BlockPos(x, y, z));
        REL_OFFSETS = tmp.toArray(BlockPos[]::new);
    }

    private static final Random RNG = ThreadLocalRandom.current();

    private static boolean prevCRA = false;
    private static int ticker = 0;
    private static BlockPos lastAnchor = BlockPos.ZERO;

    private static final Map<BlockPos, BlockState> changedBlocks = new HashMap<>();
    private static final Map<BlockPos, BlockState> changedLiquids = new HashMap<>();

    private static SchizoRenderType schizoRenderType = SchizoRenderType.ALL_BLOCKS;
    private static PsychosisType psychosisType = PsychosisType.NORMAL_PSYCHOSIS;
    private static boolean pauseWasActive = false;
    public static SoundEvent currentSchizoMusic = null;

    public Psychosis() {
        super("PSYCHOSIS", "Psychosis", 200, 0.5, 60, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performClient(Minecraft mc) {
        if (!active) {
            enablePsychosis(true);
            disablePsychosisTask = scheduler.schedule(() -> enablePsychosis(false), 20, java.util.concurrent.TimeUnit.SECONDS);
        } else {
            enablePsychosis(false);
        }
    }

    public static void enablePsychosis(boolean enable) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if (player == null) return;

        if (enable && !active) {
            active = true;
            Minecraft.getInstance().getMusicManager().stopPlaying();

            currentSchizoMusic = schizoMusicOptions.get(new Random().nextInt(schizoMusicOptions.size())).get();
            SoundManager.forcePlaySound(currentSchizoMusic, 1, 1);
            Psychosis.active = true;
            renderAllEntitiesAsRandomPlayers(true);

        } else if (!enable && active) {
            active = false;
            Minecraft.getInstance().getMusicManager().stopPlaying();
            disablePsychosisTask.cancel(false);
            if (currentSchizoMusic != null) SoundManager.stopSound(currentSchizoMusic);
            Psychosis.active = false;
            renderAllEntitiesAsRandomPlayers(false);
            HallucinationOccuredClient();
        }
    }

    @SubscribeEvent
    public void onPauseEvent(ClientPauseEvent event) {
        if (event.isPaused()) {
            pauseWasActive = active;
            enablePsychosis(false);
        } else {
            active = pauseWasActive;
            enablePsychosis(pauseWasActive);
        }
    }

    @SubscribeEvent
    public static void onClientPlayerDeathEvent(ClientPlayerDeathEvent event) {
        if (!active) return;

        enablePsychosis(false);
    }

    @SubscribeEvent
    public static void onExitEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        if (!active) return;

        enablePsychosis(false);
    }

    @SubscribeEvent
    public static void onRenderLevelStageEvent(RenderLevelStageEvent ev) {
        if (!active) return;

        renderVoid(ev);
        renderBlocks(ev);
    }

    @SubscribeEvent
    public static void onClickEvent(InputEvent.InteractionKeyMappingTriggered ev) {
        if (!active) return;

        ev.setSwingHand(true);
        if (!screenShouldShake) {
            SoundManager.playSoundRandomPitchVolume(ModSoundEventsRegistry.INVALID.get());
            Player p = Minecraft.getInstance().player;
            p.setHealth(Math.max(1, p.getHealth() - 1));
            screenShouldShake = true;
            scheduler.schedule(() -> screenShouldShake = false, 100, TimeUnit.MILLISECONDS);
        }
        ev.setCanceled(true);
    }

    private static void renderVoid(RenderLevelStageEvent ev) {
        if (!active || ev.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.clearColor(0f, 0f, 0f, 1f);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
        RenderSystem.enableDepthTest();
    }


    private static void renderBlocks(RenderLevelStageEvent ev) {
        if (!active || ev.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean periodic = ((ticker++ & 0xFF) == 0);
        boolean toggle = (prevCRA != active);
        prevCRA = active;

        boolean reroll = periodic || toggle;
        if (reroll) {
            SchizoRenderType prevType = schizoRenderType;
            schizoRenderType = SchizoRenderType.values()[RNG.nextInt(SchizoRenderType.values().length)];
            psychosisType = RNG.nextInt(20) == 0 ? PsychosisType.ROTATIONAL_PSYCHOSIS : PsychosisType.NORMAL_PSYCHOSIS;
            if (schizoRenderType != prevType) SoundManager.playSoundRandomPitchVolume(STATICSWITCH.get());
            changedBlocks.clear();
            changedLiquids.clear();
        }

        updateVisibleLists(mc, reroll);

        Map<BlockPos, BlockState> drawList = new HashMap<>(changedBlocks);
        drawList.putAll(changedLiquids);

        Camera cam = ev.getCamera();
        PoseStack ps = ev.getPoseStack();
        BlockRenderDispatcher brd = mc.getBlockRenderer();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();

        if (screenShouldShake) {
            float intensity = 0.5f;
            float dx = (RNG.nextFloat() - 0.5f) * intensity;
            float dy = (RNG.nextFloat() - 0.5f) * intensity;
            float dz = (RNG.nextFloat() - 0.5f) * intensity;

            RenderSystem.clearColor(1f, 1f, 1f, 1f);
            RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);

            ps.translate(dx, dy, dz);
        }


        final double camX = cam.getPosition().x();
        final double camY = cam.getPosition().y();
        final double camZ = cam.getPosition().z();

        for (var e : drawList.entrySet()) {
            BlockPos p = e.getKey();
            ps.pushPose();
            ps.translate(p.getX() - camX, p.getY() - camY, p.getZ() - camZ);
            if (psychosisType == PsychosisType.ROTATIONAL_PSYCHOSIS) ps.mulPose(cam.rotation());
            VertexConsumer vc = buf.getBuffer(ItemBlockRenderTypes.getRenderType(e.getValue(), false));
            brd.getModelRenderer().renderModel(ps.last(), vc, e.getValue(), brd.getBlockModel(e.getValue()), 1f, 1f, 1f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            ps.popPose();
        }

        flush(buf, RenderType.solid());
        flush(buf, RenderType.cutout());
        flush(buf, RenderType.cutoutMipped());
        flush(buf, RenderType.translucent());

        RenderSystem.enableCull();
    }

    private static void updateVisibleLists(Minecraft mc, boolean forceReroll) {
        assert mc.player != null;
        BlockPos anchor = mc.player.blockPosition();
        if (!forceReroll && anchor.equals(lastAnchor)) return;
        lastAnchor = anchor;

        Level lvl = mc.player.level();
        BlockState missing = ModBlocksRegistry.SMOOTH_BLOCK.get().defaultBlockState();

        Set<BlockPos> inRadius = new HashSet<>(REL_OFFSETS.length);
        for (BlockPos rel : REL_OFFSETS) inRadius.add(anchor.offset(rel));

        changedBlocks.keySet().removeIf(p -> !inRadius.contains(p));
        changedLiquids.keySet().removeIf(p -> !inRadius.contains(p));

        for (BlockPos p : inRadius) {
            if (changedBlocks.containsKey(p) || changedLiquids.containsKey(p)) continue;

            BlockState state = lvl.getBlockState(p);
            if (state.isAir() || isFullyHidden(p, lvl) || state.getCollisionShape(lvl, p).equals(Shapes.empty()))
                continue;

            if (state.getBlock() instanceof LiquidBlock || state.getBlock() instanceof IFluidBlock) {
                changedLiquids.put(p, Blocks.AIR.defaultBlockState());
            } else {
                BlockState finalState = switch (schizoRenderType) {
                    case MISSING_TEXTURE -> missing;
                    case ALL_BLOCKS -> BlockUtils.getRandomBlock();
                };
                changedBlocks.put(p, finalState);
            }
        }
    }

    private static boolean isFullyHidden(BlockPos pos, Level lvl) {
        for (Direction d : Direction.values()) {
            BlockPos q = pos.relative(d);
            if (!lvl.getBlockState(q).isSolidRender(lvl, q)) return false;
        }
        return true;
    }

    private static void renderDrawList(Map<BlockPos, BlockState> list, MultiBufferSource.BufferSource buf, PoseStack pose, Camera cam, BlockRenderDispatcher disp, boolean rotational, Frustum frustum) {

        final double camX = cam.getPosition().x();
        final double camY = cam.getPosition().y();
        final double camZ = cam.getPosition().z();

        for (var entry : list.entrySet()) {
            BlockPos pos = entry.getKey();
            if (frustum != null && !frustum.isVisible(new AABB(pos))) continue;

            pose.pushPose();
            pose.translate(pos.getX() - camX, pos.getY() - camY, pos.getZ() - camZ);

            if (rotational) pose.mulPose(cam.rotation());

            VertexConsumer vc = buf.getBuffer(ItemBlockRenderTypes.getRenderType(entry.getValue(), false));
            disp.getModelRenderer().renderModel(pose.last(), vc, entry.getValue(), disp.getBlockModel(entry.getValue()), 1f, 1f, 1f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
    }

    private static void flush(MultiBufferSource.BufferSource buf, RenderType type) {
        type.setupRenderState();
        buf.endBatch(type);
        type.clearRenderState();
    }


}