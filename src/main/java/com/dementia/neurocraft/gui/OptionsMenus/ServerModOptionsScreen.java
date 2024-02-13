package com.dementia.neurocraft.gui.OptionsMenus;

import com.dementia.neurocraft.config.NewWorldConfigs;
import com.dementia.neurocraft.config.ServerConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ServerModOptionsScreen extends ModOptionsScreen {
    protected final Screen lastScreen;
    private static final String lang_prefix = "neurocraft.serverConfig.";
    private boolean newWorldConfig = false;

    public ServerModOptionsScreen(Screen lastScreen) {
        super(lastScreen, Component.translatable(lang_prefix + "title"), lang_prefix);
        this.lastScreen = lastScreen;
        this.newWorldConfig = !(Minecraft.getInstance().isSingleplayer());
    }

    protected void init() {
        super.init();
        assert this.minecraft != null;

        this.list = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height - 64, 32, 25));
        putAllConfigsInMenu(this.newWorldConfig ? NewWorldConfigs.class : ServerConfigs.class);

        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (doneButton) -> {
            this.minecraft.setScreen(this.lastScreen);
        }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());

        addResetToDefaultsButton();
    }
}
