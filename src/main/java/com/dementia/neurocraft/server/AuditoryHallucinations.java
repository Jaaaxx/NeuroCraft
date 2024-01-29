package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.CHallucinationListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.util.ModTimingHandler;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Direction;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
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
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.*;
import java.util.stream.Collectors;

import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;


@EventBusSubscriber(modid = NeuroCraft.MODID)
public class AuditoryHallucinations {
    static int c = 0;
    static Map<Player, List<Entity>> playerEntityMap = new HashMap<>();

    static List<SoundEvent> randomSoundEffects = Arrays.stream(SoundEvents.class.getFields())
            .filter(field -> field.getType() == SoundEvent.class)
            .map((e) -> {
                try {
                    return (SoundEvent) e.get(null);
                } catch (IllegalAccessException ex) {
                    return null;
                }
            })
            .collect(Collectors.toList());

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (c++ % (12 * 7) == 0) {
                spawnAuditoryHallucinations(event);
            }
        }
    }

    private static void spawnAuditoryHallucinations(TickEvent.ServerTickEvent event) {
        for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
            var playerSanity = getPlayerSanity(p);
            boolean spawnHallucination = (new Random().nextInt((int) (PEAK_SANITY / 1.5)) < playerSanity);

            var pool = randomSoundEffects;
            if (spawnHallucination) {
                if (playerSanity >= PEAK_SANITY / 2) {
                    if (new Random().nextInt(2) == 1) {
                        pool = List.of(new SoundEvent[]{
                                SoundEvents.ZOMBIE_AMBIENT,
                                SoundEvents.SKELETON_AMBIENT,
                                SoundEvents.CREEPER_PRIMED,
                                SoundEvents.WARDEN_ANGRY,
                                SoundEvents.GHAST_SHOOT,
                                SoundEvents.SPIDER_AMBIENT
                        });
                    }
                }
                SoundEvent randomSound = pool.get(new Random().nextInt(pool.size()));
                PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(randomSound), p);


                if (new Random().nextInt(100) == 1) {
                    ModTimingHandler.scheduleEvent("AuditoryHallucination", 60,
                            () -> HallucinationOccured(p), true);
                }
            }
        }
    }
}