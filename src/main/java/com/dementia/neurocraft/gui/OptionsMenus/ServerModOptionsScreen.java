package com.dementia.neurocraft.gui.OptionsMenus;

//import com.dementia.neurocraft.config.ConfigSyncHandler;
import com.dementia.neurocraft.config.ServerConfigs;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ConfigTracker;
import net.minecraftforge.fml.config.ModConfig.Type;

import java.util.HashMap;
import java.util.Map;

public class ServerModOptionsScreen extends ModOptionsScreen {
    protected final Screen lastScreen;
    private static final String lang_prefix = "neurocraft.serverConfig.";

    public ServerModOptionsScreen(Screen lastScreen) {
        super(lastScreen, Component.translatable(lang_prefix + "title"), lang_prefix);
        this.lastScreen = lastScreen;
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;

        if (this.minecraft.level == null) {
            this.minecraft.setScreen(this.lastScreen);
            return;
        }

        this.list = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height - 64, 32, 25));
        putAllConfigsInMenu(ServerConfigs.class);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (doneButton) -> {
            this.minecraft.setScreen(this.lastScreen);
            if (ServerConfigs.modConfig != null) {
                ServerConfigs.modConfig.save();
//                ConfigSyncHandler.syncFeatureStates();
                ServerConfigs.SPEC.afterReload();
            }
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());

        addResetToDefaultsButton();
    }


    @Override
    protected void putAllConfigsInMenu(Class configClass) {
        super.putAllConfigsInMenu(configClass);

        Map<String, ForgeConfigSpec.ConfigValue<Boolean>> dynamic =
                ServerConfigs.getFeatureBooleanConfigs();

        HashMap<String, ForgeConfigSpec.ConfigValue<Boolean>> boolOpts = new HashMap<>(dynamic);
        addBooleans(boolOpts);
        all_options.addAll(dynamic.values());
    }
}
