package com.dementia.neurocraft.gui.Overlays;

import com.dementia.neurocraft.gui.OptionsMenus.PauseRenderScreen;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.config.ClientConfigs.SANITY_GUI;
import static com.dementia.neurocraft.gui.Overlays.SanityHudOverlayResources.*;
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class RenderSanityHudInMenu {
    public static boolean isPaused = false;
    @SubscribeEvent
    public static void registerOverlayPause(ScreenEvent.Opening event) {
        var screen = event.getNewScreen();
        if (screen == null)
            return;
        if (screen instanceof PauseScreen) {
            if (SANITY_GUI.get()) {
                isPaused = true;
                event.setNewScreen(new PauseRenderScreen(true));
            }
        }
    }
    @SubscribeEvent
    public static void registerOverlayPause(ScreenEvent.Closing event) {
        var screen = event.getScreen();
        if (screen instanceof PauseScreen) {
            if (SANITY_GUI.get()) {
                isPaused = false;
            }
        }
    }
}
