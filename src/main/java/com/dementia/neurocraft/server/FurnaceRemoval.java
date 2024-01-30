package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.FurnaceMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import net.minecraftforge.event.entity.player.PlayerContainerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Random;

import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = NeuroCraft.MODID)
public class FurnaceRemoval {
    @SubscribeEvent
    public static void onPlayerOpenFurnaceEvent(PlayerContainerEvent.Open event) {
        var pool = List.of(new MenuType[]{
                MenuType.FURNACE, MenuType.SMOKER
        });

        var container = event.getContainer();
        Player player = event.getEntity();
        boolean spawnHallucination = (new Random().nextInt(PEAK_SANITY) < getPlayerSanity(player));
        if (pool.contains(container.getType()) && spawnHallucination) {
            var ingredient_slot = container.getSlot(0);
            var ing_item = ingredient_slot.getItem();

            var fuel_slot = container.getSlot(1);
            var fuel_item = fuel_slot.getItem();

            var result_slot = container.getSlot(2);
            var result_item = result_slot.getItem();

            if (result_item.isEmpty())
                return;

            if (ing_item.isEmpty()) {
//                Ingredient res = getFurnaceIngredientFromResult(event.getEntity().level(), result_item);
//                ing_item = res == null ? null : res.getItems()[0];
//                if (ing_item == null)
                    return;
            }

            result_slot.set(ItemStack.EMPTY);
            ingredient_slot.set(ItemStack.EMPTY);
            player.getInventory().add(new ItemStack(ing_item.getItem(), result_item.getCount() + ing_item.getCount()));

            if (!fuel_item.isEmpty()) {
                fuel_slot.set(ItemStack.EMPTY);
                player.getInventory().add(fuel_item);
            }

            // set furnace to off
            container.setData(0, 0);

            HallucinationOccured(player);
        }
    }

    public static Ingredient getFurnaceIngredientFromResult(Level level, ItemStack result) {
        for (RecipeHolder<SmeltingRecipe> recipe : level.getRecipeManager().getAllRecipesFor(RecipeType.SMELTING)) {
            if (ItemStack.isSameItem(recipe.value().getResultItem(level.registryAccess()), result)) {
                return recipe.value().getIngredients().get(0);
            }
        }

        return null;
    }
}
