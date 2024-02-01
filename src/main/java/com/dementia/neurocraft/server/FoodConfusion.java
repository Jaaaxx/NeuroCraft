package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.food.Foods;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.lang.reflect.Field;
import java.util.Random;

import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;


@EventBusSubscriber(modid = NeuroCraft.MODID)
public class FoodConfusion {
    @SubscribeEvent
    public static void LivingEntityUseItemEvent(LivingEntityUseItemEvent.Finish event) {
        if (!(event.getEntity() instanceof ServerPlayer))
            return;
        var player = (Player) event.getEntity();

        var playerSanity = getPlayerSanity(player);
        boolean spawnHallucination = (new Random().nextInt(PEAK_SANITY) < playerSanity);
        if (spawnHallucination) {
            var foodProperties = event.getResultStack().getFoodProperties(event.getEntity());
            if (foodProperties != null) {
                var fd = player.getFoodData();
                fd.setFoodLevel(fd.getFoodLevel() - foodProperties.getNutrition() + 1);
                fd.setSaturation(0);
                HallucinationOccured(player);
            }
        }

        spawnHallucination = (new Random().nextInt(PEAK_SANITY) < playerSanity);
        if (spawnHallucination) {
            event.setResultStack(event.getResultStack().copyWithCount(event.getResultStack().getCount()-2));
        }
    }
}