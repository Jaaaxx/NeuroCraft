package com.dementia.neurocraft.client;

import com.mojang.blaze3d.platform.InputConstants;
import com.mojang.blaze3d.platform.InputConstants.Key;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static com.dementia.neurocraft.NeuroCraft.MODID;
import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;

@Mod.EventBusSubscriber(modid = MODID)
public class ClientControlChanges {
    private static KeyMapping[] originalKeys = null;
    private static int c = 1;

    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
        if (tick.side == LogicalSide.CLIENT && tick.phase == TickEvent.Phase.END) {
            if (c++ == 800) {
                var instance = Minecraft.getInstance();
                var player = instance.player;
                if (player == null)
                    return;
                var playerSanity = getPlayerSanity(player);

                boolean switchKeys = new Random().nextInt(PEAK_SANITY) < playerSanity;
                if (switchKeys) {
                    if (originalKeys == null) {
                        originalKeys = instance.options.keyMappings;
                    }
                    switchRandomKeys(instance);
                }
                c = 1;
            }
        }
    }

    @SubscribeEvent
    public static void onExitEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if (player == null)
            return;

        // TODO FIX THIS
        if (originalKeys != null) {
            for (var km : instance.options.keyMappings) {
                for (var ok : originalKeys) {
                    if (ok.getName().equals(km.getName())) {
                        km.setKeyModifierAndCode((KeyModifier) null, ok.getKey());
                        instance.options.setKey(km, ok.getKey());
                    }
                }
            }
            instance.options.save();
            originalKeys = null;
        }
    }

    private static void switchRandomKeys(Minecraft instance) {
        var options = instance.options;
        var option1 = getRandomKeyMapping(options, Optional.empty());
        var option2 = getRandomKeyMapping(options, Optional.of(option1));

        var key1 = option1.getKey();

        option1.setKeyModifierAndCode((KeyModifier) null, option2.getKey());
        options.setKey(option1, option2.getKey());

        option2.setKeyModifierAndCode((KeyModifier) null, key1);
        options.setKey(option2, key1);

        var player = instance.player;
        if (player != null) {
            player.sendSystemMessage(Component.literal(ChatFormatting.YELLOW +
                    "Keys " + ChatFormatting.BOLD + option1.getName().substring(4) +
                    ChatFormatting.YELLOW + " and " + ChatFormatting.BOLD + option2.getName().substring(4) +
                    ChatFormatting.YELLOW + " switched!"));
            HallucinationOccuredClient();
        }

        options.save();
    }

    public static KeyMapping getRandomKeyMapping(Options options, Optional<KeyMapping> excludeKey) {
        List<Field> keyFields = Stream.of(options.getClass().getDeclaredFields())
                .filter((field) -> KeyMapping.class.isAssignableFrom(field.getType()))
                .toList();

        while (true) {
            try {
                Field randomField = keyFields.get(new Random().nextInt(keyFields.size()));
                System.out.println(randomField);

                var keyMapping = randomField.get(options);
                if (excludeKey.isPresent() && keyMapping instanceof KeyMapping && excludeKey.get() == keyMapping)
                    continue;
                KeyMapping fin = (KeyMapping) keyMapping;
                if (fin.isUnbound())
                    continue;
                return fin;
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}
