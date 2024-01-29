package com.dementia.neurocraft.common;

import com.dementia.neurocraft.util.ServerTimingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

import java.util.Timer;
import java.util.TimerTask;

public class ClientSoundManager {
    public static boolean soundPlaying = false;
    public static Timer timer = new Timer();

    // THE PROBLEM:
    // if two things simultaneously call this, it will break
    public static boolean playSound(SoundEvent soundEvent) {
        if (soundPlaying)
            return false;

        var player = Minecraft.getInstance().player;
        if (player == null)
            return false;

        soundPlaying = true;
        player.playSound(soundEvent, (float) (Math.random() * 2.9 + 0.1), (float) (Math.random() * 4.5 + 0.5));
        timer.schedule(new TimerTask() {
            public void run() {
                soundPlaying = false;
            }
        }, 100);
        return true;
    }
}
