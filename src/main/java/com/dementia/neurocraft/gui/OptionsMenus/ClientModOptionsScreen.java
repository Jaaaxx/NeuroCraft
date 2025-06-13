package com.dementia.neurocraft.gui.OptionsMenus;

import com.dementia.neurocraft.config.ClientConfigs;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;

public class ClientModOptionsScreen extends ModOptionsScreen {
    protected final Screen lastScreen;
    private static final String lang_prefix = "neurocraft.clientConfig.";

    public ClientModOptionsScreen(Screen lastScreen) {
        super(lastScreen, Component.translatable(lang_prefix + "title"), lang_prefix);
        this.lastScreen = lastScreen;
    }

    protected void init() {
        super.init();
        assert this.minecraft != null;

        this.list = this.addRenderableWidget(new OptionsList(this.minecraft, this.width, this.height - 64, 32, 25));
        putAllConfigsInMenu(ClientConfigs.class);

        boolean inMainMenu = this.minecraft.getCurrentServer() == null;
        boolean playerControlsServer = this.minecraft.isSingleplayer() || inMainMenu;

        if (!playerControlsServer) {
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (doneButton) -> {
                this.minecraft.setScreen(this.lastScreen);
            }).bounds(this.width / 2 - 100, this.height - 27, 200, 20).build());
        } else {
            this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (doneButton) -> {
                this.minecraft.setScreen(this.lastScreen);
            }).bounds(this.width / 2 - 100, this.height - 27, 100, 20).build());

            Button serverConfigButton = Button.builder(Component.literal("Server Options"), (doneButton) -> {
                this.minecraft.setScreen(new ServerModOptionsScreen(this));
            }).bounds(this.width / 2 + 10, this.height - 27, 100, 20).build();
            if (Minecraft.getInstance().level == null) {
                serverConfigButton.active = false;
                serverConfigButton.setMessage(Component.literal("Server Options").withStyle(style -> style.withColor(0x777777)));
            }
            this.addRenderableWidget(serverConfigButton);
        }

        addResetToDefaultsButton();
    }
}
