package com.dementia.neurocraft.client;

import com.dementia.neurocraft.common.ClientSoundManager;
import com.dementia.neurocraft.server.BlockPlaceHallucinations;
import com.dementia.neurocraft.util.ModBlocksRegistry;
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
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.client.event.RenderLevelStageEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fluids.IFluidBlock;
import net.minecraftforge.fml.common.Mod;
import org.joml.Matrix4f;
import org.joml.Vector3f;
import org.lwjgl.opengl.GL11;

import java.util.*;

import static com.dementia.neurocraft.util.ModSoundEventsRegistry.STATICSWITCH;

@Mod.EventBusSubscriber(modid = com.dementia.neurocraft.Neurocraft.MODID, value = Dist.CLIENT)
public final class RandomizeTextures {

    public enum SchizoRenderType {ALL_BLOCKS, MISSING_TEXTURE}

    public enum PsychosisType {NORMAL_PSYCHOSIS, ROTATIONAL_PSYCHOSIS}

    public static volatile boolean crazyRenderingActive = false;

    private static final int RENDER_RADIUS = 20;
    private static final Random RNG = new Random();

    private static final BlockPos[] REL_OFFSETS;

    static {
        List<BlockPos> tmp = new ArrayList<>((RENDER_RADIUS * 2 + 1) * (RENDER_RADIUS * 2 + 1) * (RENDER_RADIUS * 2 + 1));
        for (int x = -RENDER_RADIUS; x <= RENDER_RADIUS; ++x)
            for (int y = -5; y <= RENDER_RADIUS; ++y)
                for (int z = -RENDER_RADIUS; z <= RENDER_RADIUS; ++z)
                    tmp.add(new BlockPos(x, y, z));
        REL_OFFSETS = tmp.toArray(BlockPos[]::new);
    }

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
        ev.setCanceled(true);
    }

    @SubscribeEvent
    public static void onRenderHallucinations(RenderLevelStageEvent ev) {
        renderVoid(ev);
        if (!crazyRenderingActive ||
                ev.getStage() != RenderLevelStageEvent.Stage.AFTER_PARTICLES) return;

        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        boolean periodic = ((ticker++ & 0xFF) == 0);
        boolean toggle = (prevCRA != crazyRenderingActive);
        prevCRA = crazyRenderingActive;

        boolean reroll = periodic || toggle;
        if (reroll) {
            SchizoRenderType prevType = schizoRenderType;
            schizoRenderType = SchizoRenderType.values()[RNG.nextInt(SchizoRenderType.values().length)];
            psychosisType = RNG.nextInt(100) == 0 ? PsychosisType.ROTATIONAL_PSYCHOSIS
                    : PsychosisType.NORMAL_PSYCHOSIS;
            if (schizoRenderType != prevType) {
                ClientSoundManager.playSoundRandomPitchVolume(STATICSWITCH.get());
            }

            changedBlocks.clear();
            changedLiquids.clear();
        }

        updateVisibleLists(mc, reroll);

        Map<BlockPos, BlockState> drawList = new HashMap<>(changedBlocks);
        drawList.putAll(changedLiquids);

        PoseStack pose = ev.getPoseStack();
        Camera cam = ev.getCamera();
        BlockRenderDispatcher disp = mc.getBlockRenderer();
        MultiBufferSource.BufferSource buf = mc.renderBuffers().bufferSource();

        mc.getMainRenderTarget().bindWrite(true);
        RenderSystem.enableDepthTest();
        RenderSystem.depthMask(true);
        RenderSystem.disableCull();

        boolean rot = psychosisType == PsychosisType.ROTATIONAL_PSYCHOSIS;
        renderDrawList(drawList, buf, pose, cam, disp, rot,
                mc.levelRenderer.getFrustum());

        flush(buf, RenderType.solid(), pose, ev.getProjectionMatrix());
        flush(buf, RenderType.cutout(), pose, ev.getProjectionMatrix());
        flush(buf, RenderType.cutoutMipped(), pose, ev.getProjectionMatrix());
        flush(buf, RenderType.translucent(), pose, ev.getProjectionMatrix());

        RenderSystem.enableCull();
    }

    private static final KeyMapping TOGGLE_VIEW = Minecraft.getInstance().options.keyTogglePerspective;

    @SubscribeEvent
    public static void onKeyMapping(InputEvent.InteractionKeyMappingTriggered ev) {
        if (crazyRenderingActive && ev.getKeyMapping() == TOGGLE_VIEW) ev.setCanceled(true);
    }

    @SubscribeEvent
    public static void onClickEvent(InputEvent.InteractionKeyMappingTriggered ev) {
        if (crazyRenderingActive) {
            ev.setSwingHand(true);
            ev.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END || !crazyRenderingActive) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.options.getCameraType() != net.minecraft.client.CameraType.FIRST_PERSON)
            mc.options.setCameraType(net.minecraft.client.CameraType.FIRST_PERSON);
    }

    private static void updateVisibleLists(Minecraft mc, boolean forceReroll) {
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
            if (state.isAir() || isFullyHidden(p, lvl)) continue;

            BlockState finalState = switch (schizoRenderType) {
                case MISSING_TEXTURE -> missing;
                case ALL_BLOCKS -> BlockPlaceHallucinations.getRandomBlock();
            };

            ((state.getBlock() instanceof LiquidBlock || state.getBlock() instanceof IFluidBlock)
                    ? changedLiquids
                    : changedBlocks).put(p, finalState);
        }
    }

    private static boolean isFullyHidden(BlockPos pos, Level lvl) {
        for (Direction d : Direction.values()) {
            BlockPos q = pos.relative(d);
            BlockState n = lvl.getBlockState(q);
            if (!n.isSolidRender(lvl, q)) return false;
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
        final double rInv = 1.0 / Math.max(1, RENDER_RADIUS);

        for (var entry : list.entrySet()) {
            BlockPos pos = entry.getKey();
            if (!frustum.isVisible(new AABB(pos))) continue;

            double dxC = pos.getX() + 0.5 - camX;
            double dyC = pos.getY() + 0.5 - camY;
            double dzC = pos.getZ() + 0.5 - camZ;
            double dist = Math.sqrt(dxC * dxC + dyC * dyC + dzC * dzC);
            int brightness = Math.max(0, 15 - (int)(15 * dist * rInv));
            int packed = (brightness << 20) | (brightness << 4);

            float dx = (float)(pos.getX() - camX);
            float dy = (float)(pos.getY() - camY);
            float dz = (float)(pos.getZ() - camZ);

            pose.pushPose();
            if (rotational) {
                Vector3f off = new Vector3f(dx, dy, dz);
                off.rotate(cam.rotation());
                pose.translate(off.x(), off.y(), off.z());
            } else {
                pose.translate(dx, dy, dz);
            }

            VertexConsumer vc = buf.getBuffer(ItemBlockRenderTypes.getRenderType(entry.getValue(), false));
            disp.getModelRenderer().renderModel(
                    pose.last(), vc, entry.getValue(),
                    disp.getBlockModel(entry.getValue()),
                    1f, 1f, 1f, packed, OverlayTexture.NO_OVERLAY);

            pose.popPose();
        }
    }


    private static void flush(MultiBufferSource.BufferSource buf,
                              RenderType type,
                              PoseStack pose,
                              Matrix4f proj) {

        if (type == RenderType.solid())
            RenderSystem.setShader(GameRenderer::getRendertypeSolidShader);
        else if (type == RenderType.cutout())
            RenderSystem.setShader(GameRenderer::getRendertypeCutoutShader);
        else if (type == RenderType.cutoutMipped())
            RenderSystem.setShader(GameRenderer::getRendertypeCutoutMippedShader);
        else if (type == RenderType.translucent())
            RenderSystem.setShader(GameRenderer::getRendertypeTranslucentShader);
        else
            RenderSystem.setShader(GameRenderer::getPositionColorLightmapShader);

        ShaderInstance sh = RenderSystem.getShader();
        if (sh.MODEL_VIEW_MATRIX != null) sh.MODEL_VIEW_MATRIX.set(pose.last().pose());
        if (sh.PROJECTION_MATRIX != null) sh.PROJECTION_MATRIX.set(proj);
        sh.apply();

        type.setupRenderState();
        buf.endBatch(type);
        type.clearRenderState();
    }
}