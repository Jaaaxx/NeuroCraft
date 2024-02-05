package com.dementia.neurocraft.gui;

import net.minecraft.resources.ResourceLocation;

import static com.dementia.neurocraft.NeuroCraft.MODID;
import static com.dementia.neurocraft.server.PlayerScaling.PEAK_SANITY;

public class SanityHudOverlayResources {
    private static final ResourceLocation GUI_V1 = new ResourceLocation(MODID, "textures/gui/gui_v1.png");
    private static final ResourceLocation GUI_V2 = new ResourceLocation(MODID, "textures/gui/gui_v2.png");
    private static final ResourceLocation GUI_V3 = new ResourceLocation(MODID, "textures/gui/gui_v3.png");
    private static final ResourceLocation GUI_V4 = new ResourceLocation(MODID, "textures/gui/gui_v4.png");
    private static final ResourceLocation GUI_V5 = new ResourceLocation(MODID, "textures/gui/gui_v5.png");
    private static final ResourceLocation GUI_V6 = new ResourceLocation(MODID, "textures/gui/gui_v6.png");
    private static final ResourceLocation GUI_V7 = new ResourceLocation(MODID, "textures/gui/gui_v7.png");
    private static final ResourceLocation GUI_V8 = new ResourceLocation(MODID, "textures/gui/gui_v8.png");

    private static final ResourceLocation GUI_H1 = new ResourceLocation(MODID, "textures/gui/gui_h1.png");
    private static final ResourceLocation GUI_H2 = new ResourceLocation(MODID, "textures/gui/gui_h2.png");
    private static final ResourceLocation GUI_H3 = new ResourceLocation(MODID, "textures/gui/gui_h3.png");
    private static final ResourceLocation GUI_H4 = new ResourceLocation(MODID, "textures/gui/gui_h4.png");
    private static final ResourceLocation GUI_H5 = new ResourceLocation(MODID, "textures/gui/gui_h5.png");
    private static final ResourceLocation GUI_H6 = new ResourceLocation(MODID, "textures/gui/gui_h6.png");
    private static final ResourceLocation GUI_H7 = new ResourceLocation(MODID, "textures/gui/gui_h7.png");
    private static final ResourceLocation GUI_H8 = new ResourceLocation(MODID, "textures/gui/gui_h8.png");

    private static final ResourceLocation[] GUI_VS = {GUI_V1, GUI_V2, GUI_V3, GUI_V4, GUI_V5, GUI_V6, GUI_V7, GUI_V8};
    private static final ResourceLocation[] GUI_HS = {GUI_H1, GUI_H2, GUI_H3, GUI_H4, GUI_H5, GUI_H6, GUI_H7, GUI_H8};

    public static final int ACT_TEX_WIDTH = 400;
    public static final int ACT_TEX_HEIGHT = 400;

    public static final int TEX_WIDTH = 25;
    public static final int TEX_HEIGHT = 25;

    public static ResourceLocation getGuiIconNormal(int sanity) {
        double percentage = (double) sanity / ((double) PEAK_SANITY / 2);
        int index = (int) (GUI_VS.length * percentage);

        index = Math.min(GUI_VS.length - 1, Math.max(0, index));

        return GUI_VS[index];
    }
    public static ResourceLocation getGuiIconActive(int sanity) {
        double percentage = (double) sanity / ((double) PEAK_SANITY / 2);
        int index = (int) (GUI_HS.length * percentage);

        index = Math.min(GUI_HS.length - 1, Math.max(0, index));

        return GUI_HS[index];
    }
}
