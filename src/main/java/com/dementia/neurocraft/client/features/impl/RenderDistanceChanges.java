package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.internal.OptionsUtils;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

public final class RenderDistanceChanges extends Feature {

    public RenderDistanceChanges() {
        super("OPTION_RENDER_DISTANCE_CHANGES", "Render Dist. Changes", 150, 0.3, 18, true, FeatureTrigger.TICK, true);
    }

    @Override
    public void performClient(Minecraft mc) {
        var options = mc.options;
        OptionsUtils.captureDefaults(options);

        int original = OptionsUtils.originalRD;
        int newRD = RNG.nextInt(Math.max(1, original - 1)) + 2;

        options.renderDistance().set(newRD);
        options.save();
        HallucinationOccuredClient();
    }
}
