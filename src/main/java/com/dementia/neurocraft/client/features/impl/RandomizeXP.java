package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

//todo fix
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class RandomizeXP extends Feature {

    private boolean xpIsRandomized = false;
    private int ticks = 0;
    private static final int xpAmount = new Random().nextInt(1, 100);

    public RandomizeXP() {
        super("RANDOMIZE_XP", "Randomized XP Values", 100, 0.3, 10, true, FeatureTrigger.TICK);
    }

    @Override
    public void performClient(Minecraft mc) {
        // XP change is managed via handle logic
    }

//    @Override
//    public void handle(PlayerTickEvent tick, int sanity) {
//        if (!isEnabled() || !tick.player.level().isClientSide) return;
//        if (tick.phase != PlayerTickEvent.Phase.END) return;
//
//        if (++ticks % 10 != 0 && !xpIsRandomized) return;
//
//        Player p = tick.player;
//
//        if (xpIsRandomized) {
//            PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
//            xpIsRandomized = false;
//        } else {
//            boolean shouldTrigger = RNG.nextInt(PEAK_SANITY) < sanity;
//            if (shouldTrigger) {
//                p.giveExperienceLevels(xpAmount);
//                xpIsRandomized = true;
//                HallucinationOccuredClient();
//            }
//        }
//    }

    public static void resetXPToServer() {
        Player player = Minecraft.getInstance().player;
        if (player != null) {
            player.giveExperienceLevels(-xpAmount);
        }
    }
}
