package com.dementia.neurocraft.common;

import com.dementia.neurocraft.util.ModTimingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.sounds.SoundEvent;

public class ClientSoundManager {
    public static boolean soundPlaying = false;

    public static boolean playSound(SoundEvent soundEvent) {
        if (soundPlaying)
            return false;

        var player = Minecraft.getInstance().player;
        if (player == null)
            return false;

        System.out.println("Playing sound!");

        player.playSound(soundEvent, (float) (Math.random() * 2.9 + 0.1), (float) (Math.random() * 4.5 + 0.5));
        soundPlaying = true;
        ModTimingHandler.scheduleEvent("SetSoundNotPlaying", 10, () -> soundPlaying = false, true);
        return true;
    }
}
