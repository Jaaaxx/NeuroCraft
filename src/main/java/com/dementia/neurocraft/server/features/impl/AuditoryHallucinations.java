package com.dementia.neurocraft.server.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccured;

public final class AuditoryHallucinations extends Feature {


    public AuditoryHallucinations() {
        super("AUDITORY_HALLUCINATIONS", "Auditory Hallucinations", 0, 0.25, 1, true, FeatureTrigger.TICK, true);
    }

    private List<SoundEvent> getRandomSFX() {
        final List<SoundEvent> RANDOM_SFX = Stream.of(SoundEvents.class.getFields()).filter(f -> f.getType() == SoundEvent.class).map(f -> {
            try {
                return (SoundEvent) f.get(null);
            } catch (Exception e) {
                return null;
            }
        }).collect(Collectors.toList());
        if (new Random().nextInt() % 2 == 0) {
            return RANDOM_SFX;
        } else {
            return List.of(SoundEvents.ZOMBIE_AMBIENT, SoundEvents.SKELETON_AMBIENT, SoundEvents.CREEPER_PRIMED, SoundEvents.WARDEN_ANGRY, SoundEvents.GHAST_SHOOT, SoundEvents.SPIDER_AMBIENT);
        }
    }

    @Override
    public void performServer(ServerPlayer player) {
        List<SoundEvent> pool = getRandomSFX();

        SoundEvent chosen = pool.get(new Random().nextInt(pool.size()));
        PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(chosen), player);
        HallucinationOccured(player, false, false);
    }
}
