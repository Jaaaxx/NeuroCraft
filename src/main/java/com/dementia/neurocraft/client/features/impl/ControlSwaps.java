package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.internal.OptionsUtils;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

public final class ControlSwaps extends Feature {

    public ControlSwaps() {
        super("OPTION_CONTROL_SWAPS", "Control Swaps", 120, 0.25, 16, true, FeatureTrigger.TICK);
    }

    @Override
    public void performClient(Minecraft mc) {
        var options = mc.options;
        OptionsUtils.captureDefaults(options);

        var first = getRandomKeyMapping(options, Optional.empty());
        var second = getRandomKeyMapping(options, Optional.of(first));

        var tmp = first.getKey();
        first.setKey(second.getKey());
        second.setKey(tmp);

        options.setKey(first, first.getKey());
        options.setKey(second, second.getKey());
        options.save();

        if (mc.player != null) {
            mc.player.sendSystemMessage(Component.literal(ChatFormatting.DARK_GRAY +
                    "You forgot your keys... " + ChatFormatting.BOLD + first.getName().substring(4) +
                    ChatFormatting.DARK_GRAY + " and " + ChatFormatting.BOLD + second.getName().substring(4) +
                    ChatFormatting.DARK_GRAY + " switched."));
        }

        HallucinationOccuredClient();
    }

    private static KeyMapping getRandomKeyMapping(net.minecraft.client.Options options, Optional<KeyMapping> exclude) {
        List<Field> fields = Stream.of(options.getClass().getDeclaredFields())
                .filter(f -> KeyMapping.class.isAssignableFrom(f.getType()))
                .toList();

        while (true) {
            try {
                Field f = fields.get(RNG.nextInt(fields.size()));
                f.setAccessible(true);
                KeyMapping mapping = (KeyMapping) f.get(options);
                if (mapping.isUnbound()) continue;
                if (exclude.isPresent() && mapping == exclude.get()) continue;
                return mapping;
            } catch (IllegalAccessException ignored) {}
        }
    }
}
