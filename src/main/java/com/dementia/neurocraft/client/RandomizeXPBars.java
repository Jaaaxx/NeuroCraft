package com.dementia.neurocraft.client;

import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SUpdatePlayerSanityPacket;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.Random;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;
import static com.dementia.neurocraft.config.ServerConfigs.RANDOMIZE_XP;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class RandomizeXPBars {
  static int c = 1;
  static boolean xpIsRandomized = false;
  static int xp = new Random().nextInt(1, 100);

  @SubscribeEvent
  public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
    if (tick.side == LogicalSide.CLIENT && tick.phase == TickEvent.Phase.END) {
      if (c++ % 10 == 0 && RANDOMIZE_XP.get() || xpIsRandomized) {
        if (xpIsRandomized) {
          PacketHandler.sendToServer(new SUpdatePlayerSanityPacket());
        } else {
          var playerSanity = getPlayerSanityClient();
          boolean switchXP = new Random().nextInt(PEAK_SANITY.get()) < playerSanity;
          if (switchXP) {
            Player p = tick.player;
            p.giveExperienceLevels(xp);
            xpIsRandomized = true;
            HallucinationOccuredClient();
          }
        }
      }
    }
  }

  public static void resetXPToServer(int xp) {
    Player player = Minecraft.getInstance().player;
    if (player == null)
      return;
    player.giveExperienceLevels(-xp);
  }
}
