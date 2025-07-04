package com.dementia.neurocraft.client.internal;

import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

import java.util.Timer;
import java.util.TimerTask;

public class SoundManager {
    public static boolean soundPlaying = false;
    public static SoundEvent currentSound = null;
    public static Timer timer = new Timer();

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
        currentSound = soundEvent;
        player.playSound(soundEvent, volume, pitch);
        timer.schedule(new TimerTask() {
            public void run() {
                soundPlaying = false;
            }
        }, 100);
        return true;
    }
    public static boolean forcePlaySound(SoundEvent soundEvent, float volume, float pitch) {
        if (!soundPlaying)
            return playSound(soundEvent, volume, pitch);

        var player = Minecraft.getInstance().player;
        if (player == null)
            return false;

        stopSound(currentSound);
        player.playSound(soundEvent, volume, pitch);
        soundPlaying = true;
        timer.schedule(new TimerTask() {
            public void run() {
                soundPlaying = false;
                currentSound = null;
            }
        }, 100);
        return true;
    }
}
