package com.dementia.neurocraft.gui.OptionsMenus;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.client.gui.screens.Screen;

public class ModVariableScreen {
    public static Screen getForgeConfigScreenContext() {
        if (Minecraft.getInstance().isPaused()) {
            return new PauseScreen(true);
        } else {
            return new net.minecraft.client.gui.screens.TitleScreen();
        }
    }
}
