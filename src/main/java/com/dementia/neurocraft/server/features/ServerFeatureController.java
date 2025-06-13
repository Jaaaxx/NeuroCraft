package com.dementia.neurocraft.server.features;

import com.dementia.neurocraft.common.features.*;
import com.dementia.neurocraft.common.internal.HallucinationTracker;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SFeatureSyncPacket;
import com.dementia.neurocraft.server.features.impl.*;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.ServerTickEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.server.internal.PlayerScalingManager.getPlayerSanity;

@Mod.EventBusSubscriber(modid = MODID)
public final class ServerFeatureController {

    private static final List<Feature> FEATURES = new ArrayList<>();

    public static void register(Feature f) {
        FEATURES.add(f);
    }

    public static List<Feature> getFeatures() {
        return Collections.unmodifiableList(FEATURES);
    }

    @SubscribeEvent
    public static void onServerTick(ServerTickEvent ev) {
        if (ev.phase != TickEvent.Phase.END || ev.side != LogicalSide.SERVER) return;
        if (ev.getServer() == null) return;

        for (ServerPlayer sp : ev.getServer().getPlayerList().getPlayers()) {
            int sanity = getPlayerSanity(sp);
            for (Feature f : ServerFeatureController.getFeatures()) {
                if (f.getTriggerType().equals(FeatureTrigger.TICK)) {
                    f.tryRunServer(sp, sanity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (!(event.getPlayer() instanceof ServerPlayer player)) return;

        for (Feature feature : FEATURES) {
            if (feature.getTriggerType() != FeatureTrigger.BLOCK_BREAK || !feature.isEnabled() || !(feature instanceof FeatureBlockBreak))
                continue;

            ((FeatureBlockBreak) feature).onBlockBreak(event);
            feature.tryRunServer(player, getPlayerSanity(player));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void onBlockPlace(BlockEvent.EntityPlaceEvent event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Feature feature : FEATURES) {
            if (feature.getTriggerType() != FeatureTrigger.BLOCK_PLACE || !feature.isEnabled() || !(feature instanceof FeatureBlockPlace))
                continue;

            ((FeatureBlockPlace) feature).onBlockPlace(event);
            feature.tryRunServer(player, getPlayerSanity(player));
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public static void LivingEntityUseItemEvent(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Feature feature : FEATURES) {
            if (feature.getTriggerType() != FeatureTrigger.FINISH_USE_ITEM || !feature.isEnabled() || !(feature instanceof FeatureLivingEntityFinishUseItem))
                continue;

            ((FeatureLivingEntityFinishUseItem) feature).onFinishUseItemEvent(event);
            feature.tryRunServer(player, getPlayerSanity(player));
            event.setCanceled(true);
        }
    }
    @SubscribeEvent
    public static void ContainerOpenEvent(PlayerContainerEvent.Open event) {
        if (!(event.getEntity() instanceof ServerPlayer player)) return;

        for (Feature feature : FEATURES) {
            if (feature.getTriggerType() != FeatureTrigger.CONTAINER_OPEN || !feature.isEnabled() || !(feature instanceof FeatureContainerOpen))
                continue;

            ((FeatureContainerOpen) feature).onContainerOpen(event);
            feature.tryRunServer(player, getPlayerSanity(player));
        }
    }
    private static final HallucinationTracker H_TRACKER = new HallucinationTracker();

    public static HallucinationTracker getHallucinationTracker() {
        return H_TRACKER;
    }
    public static Optional<Feature> getFeatureById(String id) {
        return FEATURES.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();
    }

    public static void broadcastFeatureStatesToClients(MinecraftServer server) {
        for (ServerPlayer player : server.getPlayerList().getPlayers()) {
            for (Feature feature : FEATURES) {
                PacketHandler.sendToPlayer(new SFeatureSyncPacket(feature.getId(), feature.isEnabled()), player);
            }
        }
    }

    static {
        FEATURES.add(new AuditoryHallucinations());
        FEATURES.add(new FakeBlockBreaking());
        FEATURES.add(new FakeBlockPlacing());
        FEATURES.add(new EatingDisorder());
        FEATURES.add(new FurnaceUncooking());
        FEATURES.add(new InventoryDisarray());
        FEATURES.add(new OreHallucinationVein());
        FEATURES.add(new RandomTeleportingHallucination());
        FEATURES.add(new EnemyHallucination(H_TRACKER));
    }
}
