package com.dementia.neurocraft.client;

import com.dementia.neurocraft.common.ClientSoundManager;
import com.dementia.neurocraft.server.BlockPlaceHallucinations;
import com.dementia.neurocraft.util.ModBlocksRegistry;
import com.dementia.neurocraft.util.ModSoundEventsRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Camera;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.*;
import net.minecraft.client.renderer.block.BlockRenderDispatcher;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
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
import java.util.concurrent.ThreadLocalRandom;

import static com.dementia.neurocraft.util.ModSoundEventsRegistry.STATICSWITCH;

@Mod.EventBusSubscriber(modid = com.dementia.neurocraft.Neurocraft.MODID, value = Dist.CLIENT)
public final class RandomizeTextures {

    public enum SchizoRenderType {ALL_BLOCKS, MISSING_TEXTURE}

    public enum PsychosisType {NORMAL_PSYCHOSIS, ROTATIONAL_PSYCHOSIS}

    public static volatile boolean crazyRenderingActive = false;

    public static final int RENDER_RADIUS = 12;
    public static final int Y_RENDER_RADIUS = 12;

    private static final float MAX_FOV_DEG = 110.0F;

    private static final BlockPos[] REL_OFFSETS;
    private static boolean screenShouldShake = false;

    public static Timer timer = new Timer();

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

    public static final Map<BlockPos, BlockState> changedBlocks = new HashMap<>();
    public static final Map<BlockPos, BlockState> changedLiquids = new HashMap<>();

    private static SchizoRenderType schizoRenderType = SchizoRenderType.ALL_BLOCKS;
    private static PsychosisType psychosisType = PsychosisType.NORMAL_PSYCHOSIS;

    private static void renderVoid(RenderLevelStageEvent ev) {
        if (!crazyRenderingActive || ev.getStage() != RenderLevelStageEvent.Stage.AFTER_SKY) return;
        Minecraft.getInstance().getMainRenderTarget().bindWrite(true);
        RenderSystem.disableDepthTest();
        RenderSystem.clearColor(0f, 0f, 0f, 1f);
        RenderSystem.clear(GL11.GL_COLOR_BUFFER_BIT, false);
        RenderSystem.enableDepthTest();
    }

    @SubscribeEvent
    public static void onRenderEntity(net.minecraftforge.client.event.RenderLivingEvent.Pre<?, ?> ev) {
        if (!crazyRenderingActive) return;
        assert Minecraft.getInstance().player != null;
        if (ev.getEntity().distanceTo(Minecraft.getInstance().player) >= RENDER_RADIUS) {
            ev.setCanceled(true);
        }
    }


    @SubscribeEvent
    public static void onRenderHallucinations(RenderLevelStageEvent ev) {
        renderVoid(ev);
        if (!crazyRenderingActive || ev.getStage() != RenderLevelStageEvent.Stage.AFTER_BLOCK_ENTITIES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean periodic = ((ticker++ & 0xFF) == 0);
        boolean toggle = (prevCRA != crazyRenderingActive);
        prevCRA = crazyRenderingActive;

        boolean reroll = periodic || toggle;
        if (reroll) {
            SchizoRenderType prevType = schizoRenderType;
            schizoRenderType = SchizoRenderType.values()[RNG.nextInt(SchizoRenderType.values().length)];
            psychosisType = RNG.nextInt(20) == 0 ? PsychosisType.ROTATIONAL_PSYCHOSIS : PsychosisType.NORMAL_PSYCHOSIS;
            if (schizoRenderType != prevType) ClientSoundManager.playSoundRandomPitchVolume(STATICSWITCH.get());
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
            ps.translate(p.getX() - camX,
                    p.getY() - camY,
                    p.getZ() - camZ);          // keep this one translation

            VertexConsumer vc = buf.getBuffer(
                    ItemBlockRenderTypes.getRenderType(e.getValue(), false));
            brd.getModelRenderer().renderModel(
                    ps.last(), vc, e.getValue(),
                    brd.getBlockModel(e.getValue()),
                    1f, 1f, 1f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            ps.popPose();
        }

        flush(buf, RenderType.solid());
        flush(buf, RenderType.cutout());
        flush(buf, RenderType.cutoutMipped());
        flush(buf, RenderType.translucent());

        RenderSystem.enableCull();
    }

    private static final KeyMapping TOGGLE_VIEW = Minecraft.getInstance().options.keyTogglePerspective;

    @SubscribeEvent
    public static void onClickEvent(InputEvent.InteractionKeyMappingTriggered ev) {
        if (crazyRenderingActive) {
            ev.setSwingHand(true);
            if (!screenShouldShake) {
                ClientSoundManager.playSoundRandomPitchVolume(ModSoundEventsRegistry.INVALID.get());
                Player p = Minecraft.getInstance().player;
                p.setHealth(Math.max(1, p.getHealth() - 1));
                screenShouldShake = true;
                timer.schedule(new TimerTask() {
                    public void run() {
                        screenShouldShake = false;
                    }
                }, 100);
            }
            ev.setCanceled(true);
        }
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
                    case ALL_BLOCKS -> BlockPlaceHallucinations.getRandomBlock();
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

    private static void renderDrawList(Map<BlockPos, BlockState> list,
                                       MultiBufferSource.BufferSource buf,
                                       PoseStack pose,
                                       Camera cam,
                                       BlockRenderDispatcher disp,
                                       boolean rotational,
                                       Frustum frustum) {

        final double camX = cam.getPosition().x();
        final double camY = cam.getPosition().y();
        final double camZ = cam.getPosition().z();

        for (var entry : list.entrySet()) {
            BlockPos pos = entry.getKey();
            if (frustum != null && !frustum.isVisible(new AABB(pos))) continue;

            pose.pushPose();
            pose.translate(pos.getX() - camX,
                    pos.getY() - camY,
                    pos.getZ() - camZ);

            if (rotational) pose.mulPose(cam.rotation());

            VertexConsumer vc = buf.getBuffer(ItemBlockRenderTypes.getRenderType(entry.getValue(), false));
            disp.getModelRenderer().renderModel(pose.last(), vc, entry.getValue(),
                    disp.getBlockModel(entry.getValue()), 1f, 1f, 1f, LightTexture.FULL_BRIGHT, OverlayTexture.NO_OVERLAY);
            pose.popPose();
        }
    }

    private static void flush(MultiBufferSource.BufferSource buf, RenderType type) {
        type.setupRenderState();
        buf.endBatch(type);
        type.clearRenderState();
    }


    private static boolean crazyRenderingWasActive = false;

    @SubscribeEvent
    public static void onPauseEvent(ClientPauseEvent event) {
        if (event.isPaused()) {
            crazyRenderingWasActive = crazyRenderingActive;
            crazyRenderingActive = false;
        } else {
            crazyRenderingActive = crazyRenderingWasActive;
        }
    }

    @SubscribeEvent
    public static void onClientPlayerDeath(ClientPlayerDeathEvent event) {
        crazyRenderingActive = false;
    }

    @SubscribeEvent
    public static void onExitEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        crazyRenderingActive = false;
    }
}