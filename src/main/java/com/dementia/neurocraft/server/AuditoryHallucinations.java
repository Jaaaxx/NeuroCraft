package com.dementia.neurocraft.server;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.*;
import java.util.stream.Collectors;

import static com.dementia.neurocraft.config.ServerConfigs.AUDITORY_HALLUCINATIONS;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;
import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;


@EventBusSubscriber(modid = Neurocraft.MODID)
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
            if (c++ % (20) == 0) {
                spawnAuditoryHallucinations(event);
            }
        }
    }

    private static void spawnAuditoryHallucinations(TickEvent.ServerTickEvent event) {
        if (!AUDITORY_HALLUCINATIONS.get())
            return;
        for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
            var playerSanity = getPlayerSanity(p);
            boolean spawnHallucination = (new Random().nextInt((int) (PEAK_SANITY.get() / 1.5)) < playerSanity);

            var pool = randomSoundEffects;
            if (spawnHallucination) {
                if (playerSanity >= PEAK_SANITY.get() / 2) {
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
                HallucinationOccured(p, false, false);
            }
        }
    }
}