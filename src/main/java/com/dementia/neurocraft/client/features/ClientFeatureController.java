package com.dementia.neurocraft.client.features;

import com.dementia.neurocraft.client.features.impl.*;
import com.dementia.neurocraft.client.features.impl.Psychosis;
import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureClientMobSpawn;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Mob;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.client.internal.PlayerSanityClientHandler.getPlayerSanityClient;

/**
 * Central dispatcher that owns and evaluates all registered {@link Feature}s.
 * Features may be added at static‑init time or dynamically via the public API.
 */
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class ClientFeatureController {
    private static final List<Feature> FEATURES = new ArrayList<>();

    /**
     * Register a new feature at runtime.
     */
    public static void register(Feature feature) {
        FEATURES.add(feature);
    }

    /**
     * Read‑only view for debug HUDs or config screens.
     */
    public static List<Feature> getFeatures() {
        return Collections.unmodifiableList(FEATURES);
    }

    @SubscribeEvent
    public static void onPlayerTick(PlayerTickEvent ev) {
        if (ev.side != LogicalSide.CLIENT || ev.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        int sanity = getPlayerSanityClient();
        for (Feature f : ClientFeatureController.getFeatures()) {
            if (f.getTriggerType().equals(FeatureTrigger.TICK)) {
                f.tryRunClient(mc, sanity);
            }
        }
    }

    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent ev) {
        if (ev.side != LogicalSide.CLIENT || ev.phase != TickEvent.Phase.END) return;
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;
        int sanity = getPlayerSanityClient();

        for (Feature feature : FEATURES) {
            if (feature.getTriggerType() != FeatureTrigger.CLIENT_TICK || !feature.isEnabled())
                continue;

            feature.tryRunClient(mc, sanity);
        }
    }


    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean canSpecialRenderMobs = false;
    private static boolean scheduled = false;
    @SubscribeEvent
    public static void onMobSpawnEvent(EntityJoinLevelEvent ev) {
        // Don't count mobs on world spawn
        if (!canSpecialRenderMobs && !scheduled) {
            scheduler.schedule(() -> canSpecialRenderMobs = true, 10, TimeUnit.SECONDS);
            scheduled = true;
        }

        if (!canSpecialRenderMobs)
            return;

        Minecraft mc = Minecraft.getInstance();
        var player = mc.player;
        if (player == null) return;
        int sanity = getPlayerSanityClient();


        for (Feature feature : FEATURES) {
            if (feature.getTriggerType() != FeatureTrigger.CLIENT_MOB_SPAWN || !feature.isEnabled() || !(feature instanceof FeatureClientMobSpawn))
                continue;

            if (!(ev.getEntity() instanceof Mob) || !ev.getLevel().isClientSide())
                continue;

            ((FeatureClientMobSpawn) feature).onMobSpawn(ev);
            feature.tryRunClient(mc, sanity);
        }
    }


    public static Optional<Feature> getFeatureById(String id) {
        return FEATURES.stream()
                .filter(f -> f.getId().equals(id))
                .findFirst();
    }

    static {
        FEATURES.add(new Psychosis());

        FEATURES.add(new ItemNameDementia());
        FEATURES.add(new ItemTypeDementia());

        FEATURES.add(new RandomizeHealthBars());
        FEATURES.add(new RandomizeXP());

        FEATURES.add(new EnemyHallucination());

        FEATURES.add(new FOVChanges());
        FEATURES.add(new BrightnessChanges());
        FEATURES.add(new FramerateChanges());
        FEATURES.add(new RenderDistanceChanges());
        FEATURES.add(new ControlSwaps());

        FEATURES.add(new PlayerDisorientation());

        FEATURES.add(new ClientMobSpawnRandomization());
    }

}
