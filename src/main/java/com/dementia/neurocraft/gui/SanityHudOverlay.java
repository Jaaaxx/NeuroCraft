package com.dementia.neurocraft.gui;

import com.dementia.neurocraft.util.ClientTimingHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RenderGuiOverlayEvent;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.gui.SanityHudOverlayResources.*;

public class SanityHudOverlay {
    private static boolean hurtBrainActive = false;
    public static final IGuiOverlay HUD_SANITY = ((gui, guiGraphics, partialTick, width, height) -> {
        var playerSanity = getPlayerSanityClient();
        var icon = getGuiIconNormal((int) playerSanity);


        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f);
        RenderSystem.setShaderTexture(0, icon);

        ResourceLocation texture = hurtBrainActive ? getGuiIconActive((int) playerSanity) : getGuiIconNormal((int) playerSanity);


        guiGraphics.blit(texture,
                width - TEX_WIDTH - 15, 15, 0, 0,
                TEX_WIDTH, TEX_HEIGHT, TEX_WIDTH, TEX_HEIGHT);
    });

    public static void setGUIBrainActive() {
        hurtBrainActive = true;
        ClientTimingHandler.scheduleEvent("disableHurtBrain", 10, () -> hurtBrainActive = false, true);
    }
}