package com.dementia.neurocraft;

import com.dementia.neurocraft.config.ClientConfigs;
import com.dementia.neurocraft.config.NewWorldConfigs;
import com.dementia.neurocraft.config.ServerConfigs;
import com.dementia.neurocraft.gui.OptionsMenus.ClientModOptionsScreen;
import com.dementia.neurocraft.common.ClientSoundManager;
import com.dementia.neurocraft.util.ModBlocksRegistry;
import com.dementia.neurocraft.util.ModSoundEventsRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.gui.ModListScreen;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLConstructModEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.slf4j.Logger;

import static com.dementia.neurocraft.gui.OptionsMenus.ModVariableScreen.getForgeConfigScreenContext;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Neurocraft.MODID)
public class Neurocraft {
    public static final String MODID = "neurocraft";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final DeferredRegister<Block> BLOCKS = DeferredRegister.create(ForgeRegistries.BLOCKS, MODID);
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(ForgeRegistries.ITEMS, MODID);
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    public Neurocraft() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        modEventBus.addListener(this::commonSetup);

        BLOCKS.register(modEventBus);
        ITEMS.register(modEventBus);
        CREATIVE_MODE_TABS.register(modEventBus);

        MinecraftForge.EVENT_BUS.register(this);
        ModSoundEventsRegistry.register(modEventBus);
        ModBlocksRegistry.register(modEventBus);

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC, MODID + "-client.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, NewWorldConfigs.SPEC, MODID + "-new_world.toml");
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC, MODID + "-server.toml");
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        static int c = 1;
        public static ClientSoundManager clientSoundManager;

        @SubscribeEvent
        public static void onClientSetup(FMLConstructModEvent event) {
            // Loads resources to ensure special title screen gets rendered
            event.enqueueWork(() -> {
                var instance = Minecraft.getInstance();
                if (instance.options.resourcePacks.contains("mod_resources")) {
                    instance.options.resourcePacks.remove("mod_resources");
                    instance.options.resourcePacks.add("mod_resources");
                    instance.options.save();
                }
            });

            // Registers config screens
            event.enqueueWork(() -> {
                ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ClientModOptionsScreen(new ModListScreen(getForgeConfigScreenContext()))));
            });
        }
    }
}
