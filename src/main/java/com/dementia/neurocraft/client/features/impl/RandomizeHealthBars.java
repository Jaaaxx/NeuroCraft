package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;

import java.util.Timer;
import java.util.TimerTask;

import static com.dementia.neurocraft.client.internal.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;

public final class RandomizeHealthBars extends Feature {

    private static final Timer timer = new Timer();
    private static boolean barsAreRandomized = false;

    public RandomizeHealthBars() {
        super("RANDOMIZE_BARS", "GUI Bar Dementia", 100, 0.4, 10, true, FeatureTrigger.TICK);
    }

    @Override
    public void performClient(Minecraft mc) {
        Player player = mc.player;
        if (player == null) return;

        int sanity = getPlayerSanityClient();
        boolean shouldRandomize = RNG.nextInt(PEAK_SANITY * 2) < sanity;

        if (!shouldRandomize) return;

        player.setHealth(RNG.nextInt(1, (int) player.getMaxHealth()));
        player.getFoodData().setFoodLevel(RNG.nextInt(1, 20));
        barsAreRandomized = true;

        HallucinationOccuredClient();

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                if (barsAreRandomized) {
                    PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
                    barsAreRandomized = false;
                }
            }
        }, 2000); // reset after 2 seconds (2000 ms)
    }

    public static void resetBarsToServer(int foodLevel, float healthLevel) {
        Player player = Minecraft.getInstance().player;
        if (player == null) return;

        player.getFoodData().setFoodLevel(foodLevel);
        player.setHealth(healthLevel);
    }
}
