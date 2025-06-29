package com.dementia.neurocraft.gui.Overlays;

import com.dementia.neurocraft.Neurocraft;
import net.minecraft.resources.ResourceLocation;

public class SanityHudOverlayResources {
    public static final int TEX_WIDTH = 15;
    public static final int TEX_HEIGHT = 15;
    
    // Use the existing brain texture
    public static final ResourceLocation BRAIN_TEXTURE = new ResourceLocation(Neurocraft.MODID, "textures/gui/brainy.png");
    
    public static ResourceLocation getGuiIconNormal(int sanity) {
        return BRAIN_TEXTURE;
    }
    
    public static ResourceLocation getGuiIconActive(int sanity) {
        return BRAIN_TEXTURE;
    }
    
    public static class RenderSanityHudInMenu {
        public static boolean isPaused = false;
    }
} 