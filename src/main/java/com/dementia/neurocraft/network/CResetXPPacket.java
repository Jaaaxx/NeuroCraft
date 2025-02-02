package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkEvent.ClientCustomPayloadEvent;

import java.util.function.Supplier;

import static com.dementia.neurocraft.client.RandomizeXPBars.resetXPToServer;

public class CResetXPPacket {
    int xp;

    public CResetXPPacket(int xp) {
        this.xp = xp;
    }

    public CResetXPPacket(FriendlyByteBuf buffer) {
        this(buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer) {
        buffer.writeInt(xp);
    }

  public void handle(Supplier<NetworkEvent.Context> context) {
        resetXPToServer(xp);
    }
}
