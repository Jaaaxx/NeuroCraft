package com.dementia.neurocraft;

import com.dementia.neurocraft.config.ClientConfigs;
import com.dementia.neurocraft.config.ServerConfigs;
import com.dementia.neurocraft.gui.OptionsMenus.ClientModOptionsScreen;
import com.dementia.neurocraft.client.internal.SoundManager;
import com.dementia.neurocraft.network.PacketHandler;
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
        
        // Register network packets
        PacketHandler.register();

        ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, ClientConfigs.SPEC);
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, ServerConfigs.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        LOGGER.info("HELLO FROM COMMON SETUP");
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {
        LOGGER.info("HELLO from server starting");
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents {
        static int c = 1;
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
                ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                        () -> new ConfigScreenHandler.ConfigScreenFactory((mc, screen) -> new ClientModOptionsScreen(new ModListScreen(getForgeConfigScreenContext()))));
            });
        }
    }
}
