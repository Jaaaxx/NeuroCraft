package com.dementia.neurocraft.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.event.network.CustomPayloadEvent;

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

  public void handle(CustomPayloadEvent.Context context) {
    if (context.isClientSide()) {
      resetXPToServer(xp);
      context.setPacketHandled(true);
    } else {
      context.setPacketHandled(false);
    }
  }
}
