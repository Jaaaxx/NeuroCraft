package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

import static com.dementia.neurocraft.client.RandomizeHealthBars.resetBarsToServer;
import static com.dementia.neurocraft.gui.SanityHudOverlay.setGUIBrainActive;

public class CSetClientBrainActive {
    public CSetClientBrainActive() {
    }

    public CSetClientBrainActive(FriendlyByteBuf buffer) {
        this();
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(CustomPayloadEvent.Context context) {
        if (context.isClientSide()) {
            setGUIBrainActive();
            context.setPacketHandled(true);
        } else {
            context.setPacketHandled(false);
        }
    }
}