package com.dementia.neurocraft.events;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.gui.Overlays.SanityHudOverlay;
import com.dementia.neurocraft.network.PacketHandler;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.ScreenEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;

import javax.swing.event.MenuEvent;

import static com.dementia.neurocraft.client.PlayerSanityClientHandler.getPlayerSanityClient;
import static com.dementia.neurocraft.config.ClientConfigs.SANITY_GUI;
import static com.dementia.neurocraft.gui.Overlays.SanityHudOverlayResources.*;

@Mod.EventBusSubscriber(modid = Neurocraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class CommonModEvents {
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(PacketHandler::register);
    }


    @SubscribeEvent
    public static void registerGuiOverlays(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("sanity", SanityHudOverlay.HUD_SANITY);
    }
}

