package com.dementia.neurocraft.gui.OptionsMenus;

import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.SpriteIconButton;
import net.minecraft.client.gui.screens.PauseScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static com.dementia.neurocraft.Neurocraft.MODID;

public class PauseRenderScreen extends PauseScreen {
    public PauseRenderScreen(boolean p_96308_) {
        super(p_96308_);
    }

    @Override
    protected void init() {
        super.init();
        SpriteIconButton brainButton = this.addRenderableWidget(brainy(20, (p_280834_) -> {
            assert this.minecraft != null;
            this.minecraft.setScreen(new ClientModOptionsScreen(this));
        }));
        brainButton.setPosition(this.width-30, 10);

        this.addRenderableWidget(brainButton);
    }

    private static SpriteIconButton brainy(int width, Button.OnPress onPress) {
        Component name = Component.translatable("neurocraft.clientConfig.title");
        return SpriteIconButton.builder(name, onPress, true).width(width).sprite(new ResourceLocation(MODID, "icon/brainy"), 15, 15).build();
    }
}

