package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.FeatureClientMobSpawn;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.*;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dementia.neurocraft.client.internal.EntityRandomizer.convertEntityIntoRandomPlayer;


public final class ClientMobSpawnRandomization extends FeatureClientMobSpawn {
    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private EntityJoinLevelEvent currentEvent;

    public ClientMobSpawnRandomization() {
        super("MOB_SPAWN_RANDOMIZATION", "Mob Spawn Randomization", 300, 0.3, 0, true, FeatureTrigger.CLIENT_MOB_SPAWN, false);
    }


    @Override
    public void onMobSpawn(EntityJoinLevelEvent event) {
        this.currentEvent = event;
    }

    @Override
    public void performClient(Minecraft mc) {
        Entity entity = currentEvent.getEntity();
        if (!(entity instanceof LivingEntity))
            return;
        convertEntityIntoRandomPlayer((LivingEntity) entity, true);

        scheduler.schedule(() ->
                convertEntityIntoRandomPlayer((LivingEntity) entity, false),
                15, TimeUnit.SECONDS);
    }
}
