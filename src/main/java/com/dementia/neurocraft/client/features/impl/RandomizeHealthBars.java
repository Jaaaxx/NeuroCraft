package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import static com.dementia.neurocraft.client.internal.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;

public final class RandomizeHealthBars extends Feature {
    static ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private static boolean barsAreRandomized = false;

    public RandomizeHealthBars() {
        super("RANDOMIZE_BARS", "GUI Bar Dementia", 100, 0.4, 10, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performClient(Minecraft mc) {
        Player player = mc.player;
        if (player == null) return;

        player.setHealth(RNG.nextInt(1, (int) player.getMaxHealth()));
        player.getFoodData().setFoodLevel(RNG.nextInt(1, 20));
        barsAreRandomized = true;

        scheduler.schedule(() -> {
            if (barsAreRandomized) {
                PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
                barsAreRandomized = false;
                HallucinationOccuredClient();
            }
        }, 2, TimeUnit.SECONDS);
    }

    public static void resetBarsToServer(int foodLevel, float healthLevel) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        player.getFoodData().setFoodLevel(foodLevel);
        player.setHealth(healthLevel);
    }
}
