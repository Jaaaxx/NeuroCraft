package com.dementia.neurocraft.client.internal;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Options;

public final class OptionsUtils {
    public static KeyMapping[] originalKeys = null;
    public static int originalFOV = -1;
    public static double originalBrightness = -1;
    public static int originalFramerate = -1;
    public static int originalRD = -1;

    private OptionsUtils() {}

    public static void captureDefaults(Options options) {
        if (originalKeys == null) {
            originalKeys = options.keyMappings.clone();
        }
        if (originalFOV == -1) {
            originalFOV = options.fov().get();
        }
        if (originalBrightness == -1) {
            originalBrightness = options.gamma().get();
        }
        if (originalFramerate == -1) {
            originalFramerate = options.framerateLimit().get();
        }
        if (originalRD == -1) {
            originalRD = options.renderDistance().get();
        }
    }

    public static void reset() {
        var instance = Minecraft.getInstance();
        var options = instance.options;

        if (originalKeys != null) {
            for (var km : options.keyMappings) {
                for (var ok : originalKeys) {
                    if (ok.getName().equals(km.getName())) {
                        km.setKey(ok.getKey());
                    }
                }
            }
            originalKeys = null;
        }
        if (originalFOV != -1) options.fov().set(originalFOV);
        if (originalBrightness != -1) options.gamma().set(originalBrightness);
        if (originalFramerate != -1) options.framerateLimit().set(originalFramerate);
        if (originalRD != -1) options.renderDistance().set(originalRD);

        options.save();
    }
}
