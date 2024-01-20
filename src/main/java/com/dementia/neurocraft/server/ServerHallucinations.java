package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.network.CHallucinationListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.*;

import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;


@EventBusSubscriber(modid = NeuroCraft.MODID)
public class ServerHallucinations {
    static int c = 0;
    static Map<Player, List<Entity>> playerEntityMap = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (c++ % (20 * 5) == 0) {
                runHallucinationSpawns(event);
                for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
                    getPlayerEntities(p).removeIf(Entity::isRemoved);
                    int[] entityIDList = getPlayerEntities(p).stream().mapToInt(Entity::getId).toArray();
                    PacketHandler.sendToPlayer(new CHallucinationListUpdatePacket(entityIDList), p);
                }
                c = 1;
            }
        }
    }

    private static void runHallucinationSpawns(TickEvent.ServerTickEvent event) {
        for (Player p : event.getServer().getPlayerList().getPlayers()) {
            boolean spawnHallucination = (new Random().nextInt(1000) < getPlayerSanity(p));

            Direction direction = p.getDirection();
            Vec3 pos = p.getPosition(0);
            Vec3 spawnPos = pos.relative(direction.getOpposite(), new Random().nextInt(1, 5));

            if (spawnHallucination) {
                var pool = new EntityType[]{EntityType.SKELETON
                        , EntityType.CREEPER, EntityType.ZOMBIE,
                        EntityType.ENDERMAN, EntityType.WARDEN};
                EntityType<?> entityType = pool[new Random().nextInt(pool.length)];
                Entity entity = entityType.create(p.level());


                if (entity != null) {
                    if (entityType == EntityType.SKELETON) {
                        ItemStack bow = new ItemStack(Items.BOW);
                        entity.setItemSlot(EquipmentSlot.MAINHAND, bow);
                    }
                    ((Monster) entity).setTarget(p);

                    entity.setPos(spawnPos);
                    p.level().addFreshEntity(entity);

                    playerEntityMap.putIfAbsent(p, new ArrayList<>());
                    playerEntityMap.get(p).add(entity);
                }
            }
        }
    }

    public static List<Entity> getPlayerEntities(Player player) {
        return playerEntityMap.getOrDefault(player, Collections.emptyList());
    }
}