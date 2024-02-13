package com.dementia.neurocraft.client;

import com.dementia.neurocraft.config.NewWorldConfigs;
import com.dementia.neurocraft.config.ServerConfigs;
import net.minecraft.world.level.storage.LevelResource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.event.server.ServerStartedEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static com.dementia.neurocraft.NeuroCraft.MODID;

@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public class NewWorldCommonConfig {
    @SubscribeEvent
    public static void onGenerateWorldEvent(ServerStartedEvent event) {
        if (event.getServer().isSingleplayer()) {
            Path worldPath = event.getServer().getWorldPath(LevelResource.ROOT);
            Path modLoadedFilePath = Paths.get(worldPath.toString(), "serverconfig", "world_generated");

            if (Files.notExists(modLoadedFilePath)) {
                try {
                    Files.createFile(modLoadedFilePath);
                } catch (IOException ignored) {}
            } else {
                return;
            }

            Field[] configFields = NewWorldConfigs.class.getDeclaredFields();
            for (Field cf : configFields) {
                try {
                    ForgeConfigSpec.ConfigValue option = (ForgeConfigSpec.ConfigValue) cf.get(null);
                    ((ForgeConfigSpec.ConfigValue) ServerConfigs.class.getField(cf.getName()).get(null)).set(option.get());
                } catch (ClassCastException | IllegalAccessException | NoSuchFieldException ignored) {
                }
            }
        }
    }


//    @SubscribeEvent
//    public static void onEvent(Event e) {
//        try {
//            var x = Minecraft.getInstance();
////            if (x.player == null) {
////                return;
////            }
//            LOGGER.info("Event: " + e.toString());
//        } catch (Exception ignored) {
//        }
//    }

}
