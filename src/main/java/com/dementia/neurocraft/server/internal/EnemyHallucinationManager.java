package com.dementia.neurocraft.server.internal;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.common.util.HallucinationUtils;
import com.dementia.neurocraft.network.CHallucinationListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.*;

import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;
import static com.dementia.neurocraft.config.ServerConfigs.isEnabled;
import static com.dementia.neurocraft.server.internal.PlayerScalingManager.getPlayerSanity;

@EventBusSubscriber(modid = Neurocraft.MODID)
public class EnemyHallucinationManager {
    private static int c = 0;

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        if (c++ % (20 * 8) != 0) return;

        var tracker = ServerFeatureController.getHallucinationTracker();
        for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
            tracker.get(p).removeIf(id -> {
                Entity e = p.level().getEntity(id);
                return e == null || !e.isAlive();
            });

            PacketHandler.sendToPlayer(new CHallucinationListUpdatePacket(
                    tracker.get(p).stream().mapToInt(Integer::intValue).toArray()), p);
        }

        runHallucinationSpawns(event);
        c = 1;
    }

    @SubscribeEvent
    public static void onMobSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {
        if (!isEnabled("ENEMY_HALLUCINATIONS")) return;
        if (event.getSpawnType() == MobSpawnType.CHUNK_GENERATION ||
                event.getSpawnType() == MobSpawnType.COMMAND ||
                event.getSpawnType() == MobSpawnType.SPAWN_EGG) return;

        var entity = event.getEntity();
        Player np = event.getLevel().getNearestPlayer(entity, 50);
        if (np == null) return;

        var sanity = getPlayerSanity(np);
        if (new Random().nextInt(PEAK_SANITY) < sanity) {
            ServerFeatureController.getHallucinationTracker().get(np).add(entity.getId());
        }
    }

    @SubscribeEvent
    public static void onAttackEntityEvent(AttackEntityEvent event) {
        var entity = event.getTarget();
        var player = event.getEntity();
        if (!(player instanceof ServerPlayer sp)) return;

        var tracker = ServerFeatureController.getHallucinationTracker();
        if (!tracker.get(player).contains(entity.getId())) return;

        var pos = entity.getPosition(0);
        var oldPos = player.getPosition(0);
        entity.remove(Entity.RemovalReason.KILLED);
        tracker.get(player).remove((Integer) entity.getId());

        if (new Random().nextInt(25) == 1) {
            player.setPos(pos);
            player.lookAt(EntityAnchorArgument.Anchor.EYES, oldPos);
        }

        event.setCanceled(true);
        HallucinationUtils.HallucinationOccured(player);
    }

    @SubscribeEvent
    public static void onLivingDropsEvent(LivingDropsEvent event) {
        int targetId = event.getEntity().getId();
        var tracker = ServerFeatureController.getHallucinationTracker();

        for (Player sp : event.getEntity().level().players()) {
            if (tracker.get(sp).contains(targetId)) {
                event.setCanceled(true);
                return;
            }
        }
    }

    private static void runHallucinationSpawns(TickEvent.ServerTickEvent event) {
        if (!isEnabled("ENEMY_HALLUCINATIONS")) return;

        var tracker = ServerFeatureController.getHallucinationTracker();

        for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
            var sanity = getPlayerSanity(p);
            if (new Random().nextInt((int) (PEAK_SANITY * 1.5)) >= sanity) continue;

            Direction direction = p.getDirection();
            Vec3 pos = p.getPosition(0);
            Vec3 spawnPos = pos.relative(direction.getOpposite(), new Random().nextInt(1, 5));

            var pool = new EntityType[]{
                    EntityType.ZOMBIE, EntityType.SPIDER, EntityType.SKELETON, EntityType.CREEPER,
                    EntityType.ENDERMAN, EntityType.CAVE_SPIDER, EntityType.WARDEN,
                    EntityType.RAVAGER, EntityType.WITHER
            };

            int index = Math.min(pool.length - 1,
                    (int) Math.floor(new Random().nextFloat() * sanity / ((float) PEAK_SANITY / pool.length - 1)));

            spawnEntityHallucination(pool[index], p, sanity, spawnPos);
        }
    }

    private static void spawnEntityHallucination(EntityType<?> type, Player p, int sanity, Vec3 spawnPos) {
        Entity entity = type.create(p.level());
        if (entity == null) return;

        if (type == EntityType.SKELETON)
            entity.setItemSlot(EquipmentSlot.MAINHAND, new ItemStack(Items.BOW));

        if (entity instanceof Monster m)
            m.setTarget(p);

        entity.setPos(spawnPos);
        p.level().addFreshEntity(entity);

        if (new Random().nextInt(PEAK_SANITY * 2) >= sanity)
            ServerFeatureController.getHallucinationTracker().get(p).add(entity.getId());
    }
}
