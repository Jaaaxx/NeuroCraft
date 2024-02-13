package com.dementia.neurocraft.server;

import com.dementia.neurocraft.Neurocraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

import static com.dementia.neurocraft.config.ServerConfigs.ITEMS_SWAP_POSITIONS_IN_INVENTORY;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID)
public class ServerInventoryTroubles {
    private static int c = 1;

    @SubscribeEvent
    public static void onServerTickEvent(TickEvent.ServerTickEvent event) {
        if (!ITEMS_SWAP_POSITIONS_IN_INVENTORY.get())
            return;

        if (event.side == LogicalSide.SERVER && event.phase == TickEvent.Phase.END && event.getServer() != null) {
            if (c++ % 1200 == 0) {
                for (ServerPlayer p: event.getServer().getPlayerList().getPlayers()) {
                    boolean spawnHallucination = (new Random().nextInt(PEAK_SANITY.get()) < getPlayerSanity(p));
                    if (spawnHallucination)
                        randomizePlayerInventory(p.getInventory());
                }
                c = 1;
            }
        }
    }

    public static void randomizePlayerInventory(Inventory inventory) {
        List<ItemStack> items = new ArrayList<>();

        for (int i = 0; i < 36; i++) {
            items.add(inventory.getItem(i));
            inventory.setItem(i, ItemStack.EMPTY);
        }

        Collections.shuffle(items, new Random());

        for (int i = 0; i < 36; i++) {
            inventory.setItem(i, items.get(i));
        }
    }
}
