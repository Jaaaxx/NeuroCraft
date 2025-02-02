package com.dementia.neurocraft.network;

import com.dementia.neurocraft.common.ClientSoundManager;
import com.dementia.neurocraft.config.ClientConfigs;
import com.dementia.neurocraft.util.ModSoundEventsRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.ClientCustomPayloadEvent;

import java.util.function.Supplier;

public class CAuditoryHallucinationPacket {
    private final SoundEvent sound;

    public CAuditoryHallucinationPacket(SoundEvent sound) {
        this.sound = sound;
    }

    public CAuditoryHallucinationPacket(FriendlyByteBuf buffer) {
        this(SoundEvent.readFromNetwork(buffer));
    }

    public void encode(FriendlyByteBuf buffer) {
        sound.writeToNetwork(buffer);
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        if (sound == ModSoundEventsRegistry.CONFUSED.get() && ClientConfigs.HALLUCINATION_SFX.get()) {
            ClientSoundManager.playSoundRandomPitchVolume(sound);
        }
    }
}
