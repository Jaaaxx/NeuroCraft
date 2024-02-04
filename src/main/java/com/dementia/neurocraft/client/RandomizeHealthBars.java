package com.dementia.neurocraft.client;

import com.dementia.neurocraft.common.ClientSoundManager;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.dementia.neurocraft.EnabledFeatures.*;
import static com.dementia.neurocraft.EnabledFeatures.OPTION_SCHITZOWORLD;
import static com.dementia.neurocraft.NeuroCraft.MODID;
import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.util.ModSoundEventsRegistry.schitzoMusicOptions;
import static net.minecraft.world.effect.MobEffects.BLINDNESS;


@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class RandomizeHealthBars {
    static int c = 1;
    static boolean barsAreRandomized = false;
    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
        if (tick.side == LogicalSide.CLIENT && tick.phase == TickEvent.Phase.END) {
            if (c++ % 10 == 0 && RANDOMIZE_BARS || barsAreRandomized) {
                if (barsAreRandomized) {
                    PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
                } else {
                    var playerSanity = getPlayerSanityClient();
                    boolean switchBars = new Random().nextInt(PEAK_SANITY*2) < playerSanity;
                    if (switchBars) {
                        Player p = tick.player;
                        p.setHealth(new Random().nextInt(1, (int) p.getMaxHealth()));
                        p.getFoodData().setFoodLevel(new Random().nextInt(1, 20));
                        barsAreRandomized = true;
                    }
                }
            }
        }
    }

    public static void resetBarsToServer(int foodLevel, float healthLevel) {
        Player player = Minecraft.getInstance().player;
        if (player == null)
            return;
        player.getFoodData().setFoodLevel(foodLevel);
        player.setHealth(healthLevel);
    }
}
