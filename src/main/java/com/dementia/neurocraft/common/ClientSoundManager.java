package com.dementia.neurocraft.common;

import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.event.PlayLevelSoundEvent;

import java.util.Timer;
import java.util.TimerTask;

public class ClientSoundManager {
    public static boolean soundPlaying = false;
    public static Timer timer = new Timer();

    // THE PROBLEM:
    // if two things simultaneously call this, it will break
    public static boolean playSoundRandomPitchVolume(SoundEvent soundEvent) {
        return playSound(soundEvent, (float) (Math.random() * 2.9 + 0.1), (float) (Math.random() * 4.5 + 0.5));
    }

    public static void stopSound(SoundEvent soundEvent) {
        var player = Minecraft.getInstance().player;
        if (player == null)
            return;


        Minecraft.getInstance().getSoundManager().stop(soundEvent.getLocation(), player.getSoundSource());
    }

    public static boolean playSound(SoundEvent soundEvent, float volume, float pitch) {
        if (soundPlaying)
            return false;

        var player = Minecraft.getInstance().player;
        if (player == null)
            return false;

        soundPlaying = true;
        player.playSound(soundEvent, volume, pitch);
        timer.schedule(new TimerTask() {
            public void run() {
                soundPlaying = false;
            }
        }, 100);
        return true;
    }
}
