package com.dementia.neurocraft.network;

import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.common.features.Feature;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.Optional;

@OnlyIn(Dist.CLIENT)
public class ClientPacketHandler {
    
    /**
     * Handle client feature trigger packets
     */
    public static void handleClientFeatureTrigger(String featureId) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null) return;

        Optional<Feature> feature = ClientFeatureController.getFeatureById(featureId);
        if (feature.isPresent()) {
            feature.get().performClient(mc);
        }
    }
    
    /**
     * Handle config GUI refresh on client
     */
    public static void handleConfigGUIRefresh() {
        var mc = Minecraft.getInstance();
        var currentScreen = mc.screen;
        if (currentScreen != null && currentScreen.getClass().getName().contains("ModOptionsScreen")) {
            // Force screen refresh by rebuilding widgets
            currentScreen.init(mc, currentScreen.width, currentScreen.height);
        }
    }
} 