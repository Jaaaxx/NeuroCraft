package com.dementia.neurocraft.gui.OptionsMenus;

import com.dementia.neurocraft.config.ClientConfigs;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ClientModOptionsScreen extends ModOptionsScreen {
    private static final String lang_prefix = "neurocraft.clientConfig.";

    public ClientModOptionsScreen(Screen lastScreen) {
        super(lastScreen, Component.translatable(lang_prefix + "title"), lang_prefix);
    }

    @Override
    protected void init() {
        super.init();
        assert this.minecraft != null;

        this.list = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height - 64, 32, 25));
        putAllConfigsInMenu(ClientConfigs.class);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (doneButton) -> {
            this.minecraft.setScreen(this.lastScreen);
            // Client configs are saved automatically when changed
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());

        addResetToDefaultsButton();
    }
} 