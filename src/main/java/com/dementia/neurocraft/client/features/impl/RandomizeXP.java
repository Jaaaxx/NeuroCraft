package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class RandomizeXP extends Feature {
    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);

    private boolean xpIsRandomized = false;
    private static int xpAmount = 0;

    public RandomizeXP() {
        super("RANDOMIZE_XP", "Randomized XP Values", 100, 0.3, 10, true, FeatureTrigger.TICK, true);
    }


    @Override
    public void performClient(Minecraft mc) {
        Player player = mc.player;
        if (player == null) return;

        xpAmount = RNG.nextInt(1, 100);
        player.giveExperienceLevels(xpAmount);
        xpIsRandomized = true;

        scheduler.schedule(() -> {
            if (xpIsRandomized) {
                xpIsRandomized = false;
                player.giveExperienceLevels(-xpAmount);
                HallucinationOccuredClient();
            }
        }, 2, TimeUnit.SECONDS);
    }
}
