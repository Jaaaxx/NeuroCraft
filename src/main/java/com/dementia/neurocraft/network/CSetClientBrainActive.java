package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.ClientCustomPayloadEvent;

import java.util.function.Supplier;

import static com.dementia.neurocraft.gui.Overlays.SanityHudOverlay.setGUIBrainActive;

public class CSetClientBrainActive {
    public CSetClientBrainActive() {
    }

    public CSetClientBrainActive(FriendlyByteBuf buffer) {
        this();
    }

    public void encode(FriendlyByteBuf buffer) {
    }

    public void handle(Supplier<NetworkEvent.Context> context) {
        setGUIBrainActive();
    }
}