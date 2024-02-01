package com.dementia.neurocraft.network;

import com.dementia.neurocraft.common.ClientSoundManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isClientSide()) {
            ClientSoundManager.playSoundRandomPitchVolume(sound);
        } else {
            context.setPacketHandled(false);
        }
    }
}
