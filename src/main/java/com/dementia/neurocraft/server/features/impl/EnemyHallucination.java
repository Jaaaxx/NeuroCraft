// com.dementia.neurocraft.server.features.impl.EnemyHallucination.java
package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.common.internal.HallucinationTracker;
import com.dementia.neurocraft.network.CHallucinationListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;
import static com.dementia.neurocraft.server.internal.PlayerScalingManager.getPlayerSanity;


public final class EnemyHallucination extends Feature {

    private final HallucinationTracker tracker;

    public EnemyHallucination(HallucinationTracker commonTracker) {
        super("ENEMY_HALLUCINATIONS","Mob Hallucinations",300,0.3,8,true, FeatureTrigger.TICK);
        this.tracker = commonTracker;
    }

    @Override
    public void performServer(ServerPlayer player) {
        int sanity = getPlayerSanity(player);
        if (RNG.nextInt((int)(PEAK_SANITY*1.5)) >= sanity) return;

        // choose mob + spawn position
        Direction dir   = player.getDirection();
        Vec3 origin     = player.getPosition(0);
        Vec3 spawnPos   = origin.relative(dir.getOpposite(), RNG.nextInt(1,5));

        EntityType<?>[] pool = {
                EntityType.ZOMBIE, EntityType.SPIDER, EntityType.SKELETON,
                EntityType.CREEPER, EntityType.ENDERMAN, EntityType.CAVE_SPIDER,
                EntityType.WARDEN, EntityType.RAVAGER, EntityType.WITHER
        };

        EntityType<?> type = pool[Math.min(pool.length-1,
                (int)Math.floor(RNG.nextFloat((float)sanity /
                        ((float)PEAK_SANITY / (pool.length-1)))) )];

        Entity mob = type.create(player.level());
        if (mob == null) return;

        /* cosmetic setup */
        if (type == EntityType.SKELETON)
            mob.setItemSlot(EquipmentSlot.MAINHAND,new ItemStack(Items.BOW));
        if (mob instanceof Monster monster)
            monster.setTarget(player);

        mob.setPos(spawnPos);
        player.level().addFreshEntity(mob);

        // decide if hallucination
        if (RNG.nextInt(PEAK_SANITY*2) >= sanity)
            tracker.addHallucination(player, mob);

        // sync list every tick interval
        int[] ids = tracker.get(player).stream().mapToInt(Integer::intValue).toArray();
        PacketHandler.sendToPlayer(new CHallucinationListUpdatePacket(ids), player);

        HallucinationOccured(player,false,false);
    }
}
