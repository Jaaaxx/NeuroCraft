package com.dementia.neurocraft.gui.Overlays;

import com.dementia.neurocraft.gui.OptionsMenus.PauseRenderScreen;
import com.dementia.neurocraft.util.ClientTimingHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

import static com.dementia.neurocraft.config.ClientConfigs.SANITY_GUI;
import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.gui.Overlays.SanityHudOverlayResources.*;

public class SanityHudOverlay {
    private static boolean hurtBrainActive = false;
    public static final IGuiOverlay HUD_SANITY = ((gui, guiGraphics, partialTick, width, height) -> {
        if (SANITY_GUI.get() && !RenderSanityHudInMenu.isPaused) {
            var playerSanity = getPlayerSanityClient();
            ResourceLocation texture = hurtBrainActive ? getGuiIconActive((int) playerSanity) : getGuiIconNormal((int) playerSanity);

            RenderSystem.setShader(GameRenderer::getPositionTexShader);
            RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
            RenderSystem.setShaderTexture(0, texture);

            guiGraphics.blit(texture,
                    width - TEX_WIDTH - 15, 15, 0, 0,
                    TEX_WIDTH, TEX_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
        }
    });

    public static void setGUIBrainActive() {
        hurtBrainActive = true;
        ClientTimingHandler.scheduleEvent("disableHurtBrain", 10, () -> hurtBrainActive = false, true);
    }
}