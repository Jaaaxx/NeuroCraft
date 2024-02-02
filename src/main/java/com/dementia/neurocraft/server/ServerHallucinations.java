package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.network.CHallucinationListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.commands.arguments.EntityAnchorArgument;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.living.MobSpawnEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.*;
import java.util.function.Predicate;

import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;


@EventBusSubscriber(modid = NeuroCraft.MODID)
public class ServerHallucinations {
    static int c = 0;
    static Map<Player, List<Entity>> playerEntityMap = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (c++ % (20 * 2) == 0) {
                runHallucinationSpawns(event);
                for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
                    sendNewHallucinationListPacket(p);
                }
                c = 1;
            }
        }
    }

    private static void sendNewHallucinationListPacket(ServerPlayer p) {
        getPlayerEntities(p).removeIf(Entity::isRemoved);
        getPlayerEntities(p).removeIf((e) -> !e.isAlive());
        int[] entityIDList = getPlayerEntities(p).stream().mapToInt(Entity::getId).toArray();
        PacketHandler.sendToPlayer(new CHallucinationListUpdatePacket(entityIDList), p);
    }

    @SubscribeEvent
    public static void onMobSpawnEvent(MobSpawnEvent.FinalizeSpawn event) {
        if (event.getSpawnType() != MobSpawnType.CHUNK_GENERATION &&
            event.getSpawnType() != MobSpawnType.COMMAND &&
            event.getSpawnType() != MobSpawnType.SPAWN_EGG) {
            var entity = event.getEntity();
            Player np = event.getLevel().getNearestPlayer(entity, 50);
            if (np != null) {
                playerEntityMap.putIfAbsent(np, new ArrayList<>());

                // Create hallucination
                if (new Random().nextInt(PEAK_SANITY) < getPlayerSanity(np)) {
                    playerEntityMap.get(np).add(entity);
                }
            }
        }
    }

    @SubscribeEvent
    public static void onAttackEntityEvent(AttackEntityEvent event) {
        var entity = event.getTarget();
        var player = event.getEntity();
        var playerMap = playerEntityMap.get(player);
        if (playerMap == null || player.getServer() == null || entity == null || entity.isRemoved() || !entity.isAlive()) {
            return;
        }

        if (playerMap.contains(entity)) {
            var pos = entity.getPosition(0);
            var oldPos = player.getPosition(0);
            entity.remove(Entity.RemovalReason.KILLED);
            ServerPlayer sp = player.getServer().getPlayerList().getPlayer(player.getUUID());
            if (sp != null) {
                sendNewHallucinationListPacket(sp);
            }

            // Randomly rotate player on destroying hallucination
            if (new Random().nextInt(25) == 1) {
                player.setPos(pos);
                player.lookAt(EntityAnchorArgument.Anchor.EYES, oldPos);
            }
            event.setCanceled(true);
            HallucinationOccured(player);
        }
    }

    private static void runHallucinationSpawns(TickEvent.ServerTickEvent event) {
        for (Player p : event.getServer().getPlayerList().getPlayers()) {
            var playerSanity = getPlayerSanity(p);
            boolean spawnHallucination = (new Random().nextInt((int) (PEAK_SANITY*1.5)) < playerSanity);

            Direction direction = p.getDirection();
            Vec3 pos = p.getPosition(0);
            Vec3 spawnPos = pos.relative(direction.getOpposite(), new Random().nextInt(1, 5));

            if (spawnHallucination) {
                var pool = new EntityType[]{
                        EntityType.ZOMBIE,
                        EntityType.SPIDER,
                        EntityType.SKELETON,
                        EntityType.CREEPER,
                        EntityType.ENDERMAN,
                        EntityType.CAVE_SPIDER,
                        EntityType.WARDEN,
                        EntityType.RAVAGER,
                        EntityType.WITHER
                };
                EntityType<?> entityType = pool[Math.min(pool.length-1, (int) Math.floor(new Random().nextFloat((float) playerSanity / ((float) PEAK_SANITY / pool.length - 1))))];
                spawnEntityHallucination(entityType, p, playerSanity, spawnPos);
            }

        }
    }

    private static void spawnEntityHallucination(EntityType<?> entityType, Player p, long sanity, Vec3 spawnPos) {
        Entity entity = entityType.create(p.level());


        if (entity != null) {
            // TODO SET DROPS TO NULL & MAKE ENTITIES DISSAPEAR OUTSIDE OF RADIUS / PICKUP FURNACE NICK ITEMS
            if (entityType == EntityType.SKELETON) {
                ItemStack bow = new ItemStack(Items.BOW);
                entity.setItemSlot(EquipmentSlot.MAINHAND, bow);
            }

            var captureDrops = entity.captureDrops();
            if (captureDrops != null)
                captureDrops.clear();

            if (entity instanceof Monster)
                ((Monster) entity).setTarget(p);
            entity.setPos(spawnPos);
            p.level().addFreshEntity(entity);
            playerEntityMap.putIfAbsent(p, new ArrayList<>());

            // Create hallucination
            if (new Random().nextInt(PEAK_SANITY * 2) >= sanity) {
                playerEntityMap.get(p).add(entity);
            }
        }
    }

    public static List<Entity> getPlayerEntities(Player player) {
        return playerEntityMap.getOrDefault(player, Collections.emptyList());
    }
}