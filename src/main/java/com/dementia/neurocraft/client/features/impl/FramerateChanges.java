package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.internal.OptionsUtils;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

public final class FramerateChanges extends Feature {

    public FramerateChanges() {
        super("OPTION_FRAMERATE_CHANGES", "Framerate Changes", 120, 0.2, 14, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performClient(Minecraft mc) {
        var options = mc.options;
        OptionsUtils.captureDefaults(options);

        int current = options.framerateLimit().get();
        if (current == OptionsUtils.originalFramerate) {
            options.framerateLimit().set(10);
            HallucinationOccuredClient();
        } else {
            options.framerateLimit().set(OptionsUtils.originalFramerate);
        }

        options.save();
    }
}
