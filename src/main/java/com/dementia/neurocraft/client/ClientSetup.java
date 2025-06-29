package com.dementia.neurocraft.client;

import com.dementia.neurocraft.client.internal.SoundManager;
import com.dementia.neurocraft.config.ClientConfigs;
import com.dementia.neurocraft.gui.OptionsMenus.ClientModOptionsScreen;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;

import static com.dementia.neurocraft.Neurocraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ClientSetup {
    public static SoundManager soundManager;

    @SubscribeEvent
    public static void onClientSetup(FMLConstructModEvent event) {
        event.enqueueWork(() -> {
            var instance = Minecraft.getInstance();
            if (instance.options.resourcePacks.contains("mod_resources")) {
                instance.options.resourcePacks.remove("mod_resources");
                instance.options.resourcePacks.add("mod_resources");
                instance.options.save();
            }
        });

                    event.enqueueWork(() -> {
                // Register client config
                ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC);
                
                // Register config screen
                ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ClientModOptionsScreen(screen)));
            });
    }
} 