package com.dementia.neurocraft.network;

import com.dementia.neurocraft.NeuroCraft;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private int packetID = 0;
    public static final SimpleChannel INSTANCE = ChannelBuilder.named(
            new ResourceLocation(NeuroCraft.MODID, "main"))
            .serverAcceptedVersions((status, version) -> true)
            .clientAcceptedVersions((status, version) -> true)
            .networkProtocolVersion(1)
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(CHallucinationListUpdatePacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CHallucinationListUpdatePacket::encode)
                .decoder(CHallucinationListUpdatePacket::new)
                .consumerMainThread(CHallucinationListUpdatePacket::handle)
                .add();
        INSTANCE.messageBuilder(SRemoveHallucinationPacket.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SRemoveHallucinationPacket::encode)
                .decoder(SRemoveHallucinationPacket::new)
                .consumerMainThread(SRemoveHallucinationPacket::handle)
                .add();
        INSTANCE.messageBuilder(CAuditoryHallucinationPacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CAuditoryHallucinationPacket::encode)
                .decoder(CAuditoryHallucinationPacket::new)
                .consumerMainThread(CAuditoryHallucinationPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(msg, PacketDistributor.SERVER.noArg());
    }
    public static void sendToPlayer(Object msg, ServerPlayer player) {
        INSTANCE.send(msg, PacketDistributor.PLAYER.with(player));
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(msg, PacketDistributor.ALL.noArg());
    }
}
