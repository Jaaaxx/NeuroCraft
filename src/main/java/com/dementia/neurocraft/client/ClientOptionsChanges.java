package com.dementia.neurocraft.client;

import com.dementia.neurocraft.common.ClientSoundManager;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.settings.KeyModifier;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.common.Mod;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Stream;

import static com.dementia.neurocraft.config.ServerConfigs.*;
import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;
import static com.dementia.neurocraft.config.ServerConfigs.PEAK_SANITY;
import static net.minecraft.world.effect.MobEffects.BLINDNESS;
import static com.dementia.neurocraft.util.ModSoundEventsRegistry.schitzoMusicOptions;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class ClientOptionsChanges {
    private static KeyMapping[] originalKeys = null;
    private static int originalFOV = -1;
    private static double originalBrightness = -1;
    private static int originalFramerate = -1;
    private static int originalRD = -1;
    private static int c = 540;
    public static SoundEvent currentSchitzoMusic = null;



    @SubscribeEvent
    public static void onPlayerTickEvent(TickEvent.PlayerTickEvent tick) {
        if (tick.side == LogicalSide.CLIENT && tick.phase == TickEvent.Phase.END) {
            // FOV Changes
            if (c++ % (20 * 10) == 0 && OPTION_FOV_CHANGES.get()) {
                var instance = Minecraft.getInstance();
                var player = instance.player;
                if (player == null)
                    return;
                var playerSanity = getPlayerSanityClient();

                boolean switchFOV = new Random().nextInt(PEAK_SANITY.get()) < playerSanity;
                if (switchFOV) {
                    var fov = instance.options.fov().get();
                    if (originalFOV == -1) {
                        originalFOV = fov;
                    }
                    int min_fov = 30;
                    int max_fov = 110;
                    int randomNum = new Random().nextInt((max_fov - min_fov) + 1) + min_fov;
                    instance.options.fov().set(randomNum);
                    HallucinationOccuredClient();
                    instance.options.save();
                }
            }


            // Brightness Changes
            if (c % (20 * 12) == 0 && OPTION_BRIGHTNESS_CHANGES.get()) {
                var instance = Minecraft.getInstance();
                var player = instance.player;
                if (player == null)
                    return;
                var playerSanity = getPlayerSanityClient();

                boolean switchBrightness = new Random().nextInt(PEAK_SANITY.get()) < playerSanity;
                if (switchBrightness) {
                    var brightness = instance.options.gamma().get();
                    if (originalBrightness == -1) {
                        originalBrightness = brightness;
                    }
                    double randomNum = new Random().nextDouble();
                    instance.options.gamma().set(randomNum);
                    HallucinationOccuredClient();
                    instance.options.save();
                }
            }


            // Framerate Changes
            if (c % (20 * 14) == 0 && OPTION_FRAMERATE_CHANGES.get()) {
                var instance = Minecraft.getInstance();
                var player = instance.player;
                if (player == null)
                    return;
                var playerSanity = getPlayerSanityClient();

                boolean switchFramerate = new Random().nextInt(PEAK_SANITY.get() * 2) < playerSanity;
                int framerate = instance.options.framerateLimit().get();
                if (originalFramerate == -1) {
                    originalFramerate = framerate;
                }
                if (switchFramerate || framerate != originalFramerate) {
                    if (framerate == originalFramerate) {
                        instance.options.framerateLimit().set(10);
                        HallucinationOccuredClient();
                    } else {
                        instance.options.framerateLimit().set(originalFramerate);
                    }
                    instance.options.save();
                }
            }

            // Control swaps
            if (c % (20 * 16) == 0 && OPTION_CONTROL_SWAPS.get()) {
                var instance = Minecraft.getInstance();
                var player = instance.player;
                if (player == null)
                    return;
                var playerSanity = getPlayerSanityClient();
                boolean switchKeys = new Random().nextInt(PEAK_SANITY.get()) < playerSanity;
                if (switchKeys) {
                    if (originalKeys == null) {
                        originalKeys = instance.options.keyMappings;
                    }
                    switchRandomKeys(instance);
                    instance.options.save();
                }
            }


            // Render Distance Changes
            if (c % (20 * 18) == 0 && OPTION_RENDER_DISTANCE_CHANGES.get()) {
                var instance = Minecraft.getInstance();
                var player = instance.player;
                if (player == null)
                    return;
                var playerSanity = getPlayerSanityClient();

                boolean switchRD = new Random().nextInt(PEAK_SANITY.get()) < playerSanity;
                if (switchRD) {
                    var rd = instance.options.renderDistance().get();
                    if (originalRD == -1) {
                        originalRD = rd;
                    }
                    int min = 2;
                    int max = originalRD;
                    int randomNum = new Random().nextInt((max - min) + 1) + min;
                    instance.options.renderDistance().set(randomNum);
                    HallucinationOccuredClient();
                    instance.options.save();
                }
            }


            // Random Changes
            if (c % (20 * 30) == 0 && OPTION_SCHITZOWORLD.get()) {
                var instance = Minecraft.getInstance();
                var player = instance.player;
                if (player == null)
                    return;
                var playerSanity = getPlayerSanityClient();
                boolean schitzoMode = new Random().nextInt(PEAK_SANITY.get()) < playerSanity && new Random().nextInt(PEAK_SANITY.get()) < playerSanity;
                if (schitzoMode || RandomizeTextures.crazyRenderingActive) {
                    if (!RandomizeTextures.crazyRenderingActive) {
                        player.addEffect(new MobEffectInstance(BLINDNESS, MobEffectInstance.INFINITE_DURATION, 3, false, false, false));
                        currentSchitzoMusic = schitzoMusicOptions.get(new Random().nextInt(schitzoMusicOptions.size())).get();
                        ClientSoundManager.forcePlaySound(currentSchitzoMusic, 1, 1);
                        RandomizeTextures.crazyRenderingActive = true;
                    } else {
                        player.removeEffectNoUpdate(BLINDNESS);
                        player.removeEffect(BLINDNESS);
                        if (currentSchitzoMusic != null)
                            ClientSoundManager.stopSound(currentSchitzoMusic);
                        RandomizeTextures.crazyRenderingActive = false;
                        HallucinationOccuredClient();
                    }
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

        resetToOriginals();
    }

    private static void resetToOriginals() {
        var instance = Minecraft.getInstance();
        var player = instance.player;
        if (player == null)
            return;

        if (originalKeys != null) {
            for (var km : instance.options.keyMappings) {
                for (var ok : originalKeys) {
                    if (ok.getName().equals(km.getName())) {
                        km.setKeyModifierAndCode((KeyModifier) null, ok.getKey());
                        instance.options.setKey(km, ok.getKey());
                    }
                }
            }
            originalKeys = null;
        }

        if (originalFOV != -1) {
            instance.options.fov().set(originalFOV);
        }

        if (originalBrightness != -1) {
            instance.options.gamma().set(originalBrightness);
        }

        if (originalFramerate != -1) {
            instance.options.framerateLimit().set(originalFramerate);
        }

        if (originalRD != -1) {
            instance.options.renderDistance().set(originalRD);
        }

        instance.options.save();
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
            player.sendSystemMessage(Component.literal(ChatFormatting.DARK_GRAY +
                    "You forgot your keys... " + ChatFormatting.BOLD + option1.getName().substring(4) +
                    ChatFormatting.DARK_GRAY + " and " + ChatFormatting.BOLD + option2.getName().substring(4) +
                    ChatFormatting.DARK_GRAY + " switched."));
            HallucinationOccuredClient();
        }
    }

    public static KeyMapping getRandomKeyMapping(Options options, Optional<KeyMapping> excludeKey) {
        List<Field> keyFields = Stream.of(options.getClass().getDeclaredFields())
                .filter((field) -> KeyMapping.class.isAssignableFrom(field.getType()))
                .toList();

        while (true) {
            try {
                Field randomField = keyFields.get(new Random().nextInt(keyFields.size()));

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
