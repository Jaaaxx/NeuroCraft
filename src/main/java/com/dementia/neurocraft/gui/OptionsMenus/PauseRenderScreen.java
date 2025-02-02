package com.dementia.neurocraft.gui.OptionsMenus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.dementia.neurocraft.Neurocraft.MODID;

public class PauseRenderScreen extends PauseScreen {
    public PauseRenderScreen(boolean p96308) {
        super(p96308);
    }

    @Override
    protected void init() {
        super.init();
        ImageButton brainButton = this.addRenderableWidget(brainy(this.width - 30, 10, (p280834) -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new ClientModOptionsScreen(this));
        }));

        this.addRenderableWidget(brainButton);
    }

    private static ImageButton brainy(int x, int y, Button.OnPress onPress) {
        ResourceLocation brainyTexture = new ResourceLocation(MODID, "textures/gui/brainy.png"); // Update the path if needed
        int width = 20;
        int height = 20;
        int textureX = 0;
        int textureY = 0;

        return new ImageButton(x, y, width, height, textureX, textureY, height, brainyTexture, onPress);
    }
}