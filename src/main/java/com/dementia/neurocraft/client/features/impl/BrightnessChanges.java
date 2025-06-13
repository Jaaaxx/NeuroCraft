package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.internal.OptionsUtils;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

public final class BrightnessChanges extends Feature {

    public BrightnessChanges() {
        super("OPTION_BRIGHTNESS_CHANGES", "Brightness Changes", 100, 0.3, 12, true, FeatureTrigger.TICK);
    }

    @Override
    public void performClient(Minecraft mc) {
        var options = mc.options;
        OptionsUtils.captureDefaults(options);

        double gamma = RNG.nextDouble();
        options.gamma().set(gamma);
        options.save();
        HallucinationOccuredClient();
    }
}
