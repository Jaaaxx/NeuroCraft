package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.internal.OptionsUtils;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import net.minecraft.client.Minecraft;

import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

public final class FOVChanges extends Feature {

    public FOVChanges() {
        super("OPTION_FOV_CHANGES", "FOV Changes", 100, 0.3, 10, true, FeatureTrigger.TICK);
    }

    @Override
    public void performClient(Minecraft mc) {
        var options = mc.options;
        OptionsUtils.captureDefaults(options);

        int newFov = 30 + RNG.nextInt(81);
        options.fov().set(newFov);
        options.save();
        HallucinationOccuredClient();
    }
}
