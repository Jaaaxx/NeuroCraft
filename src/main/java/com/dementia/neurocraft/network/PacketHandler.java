package com.dementia.neurocraft.network;

import com.dementia.neurocraft.Neurocraft;
import com.dementia.neurocraft.util.ServerTimingHandler;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.*;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class PacketHandler {
    private static final String PROTOCOL_VERSION = "1";
    private static int packetID = 0;
    public static final SimpleChannel INSTANCE = NetworkRegistry.ChannelBuilder.named(
                    new ResourceLocation(Neurocraft.MODID, "main"))
            .serverAcceptedVersions(version -> true)
            .clientAcceptedVersions(version -> true)
            .networkProtocolVersion(() -> "1")
            .simpleChannel();

    public static void register() {
        INSTANCE.messageBuilder(CHallucinationListUpdatePacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CHallucinationListUpdatePacket::encode)
                .decoder(CHallucinationListUpdatePacket::new)
                .consumerMainThread(CHallucinationListUpdatePacket::handle)
                .add();
        INSTANCE.messageBuilder(SRemoveHallucinationPacket.class, packetID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SRemoveHallucinationPacket::encode)
                .decoder(SRemoveHallucinationPacket::new)
                .consumerMainThread(SRemoveHallucinationPacket::handle)
                .add();
        INSTANCE.messageBuilder(CAuditoryHallucinationPacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CAuditoryHallucinationPacket::encode)
                .decoder(CAuditoryHallucinationPacket::new)
                .consumerMainThread(CAuditoryHallucinationPacket::handle)
                .add();
        INSTANCE.messageBuilder(SRefreshClientBlockList.class, packetID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SRefreshClientBlockList::encode)
                .decoder(SRefreshClientBlockList::new)
                .consumerMainThread(SRefreshClientBlockList::handle)
                .add();
        INSTANCE.messageBuilder(CHallBlockListUpdatePacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CHallBlockListUpdatePacket::encode)
                .decoder(CHallBlockListUpdatePacket::new)
                .consumerMainThread(CHallBlockListUpdatePacket::handle)
                .add();
        INSTANCE.messageBuilder(SForceBlockUpdatePacket.class, packetID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SForceBlockUpdatePacket::encode)
                .decoder(SForceBlockUpdatePacket::new)
                .consumerMainThread(SForceBlockUpdatePacket::handle)
                .add();
        INSTANCE.messageBuilder(CUpdatePlayerSanityPacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CUpdatePlayerSanityPacket::encode)
                .decoder(CUpdatePlayerSanityPacket::new)
                .consumerMainThread(CUpdatePlayerSanityPacket::handle)
                .add();
        INSTANCE.messageBuilder(SUpdatePlayerSanityPacket.class, packetID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SUpdatePlayerSanityPacket::encode)
                .decoder(SUpdatePlayerSanityPacket::new)
                .consumerMainThread(SUpdatePlayerSanityPacket::handle)
                .add();
        INSTANCE.messageBuilder(CResetBarsPacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CResetBarsPacket::encode)
                .decoder(CResetBarsPacket::new)
                .consumerMainThread(CResetBarsPacket::handle)
                .add();
        INSTANCE.messageBuilder(SResetBarsPacket.class, packetID++, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SResetBarsPacket::encode)
                .decoder(SResetBarsPacket::new)
                .consumerMainThread(SResetBarsPacket::handle)
                .add();
        INSTANCE.messageBuilder(CSetClientBrainActive.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CSetClientBrainActive::encode)
                .decoder(CSetClientBrainActive::new)
                .consumerMainThread(CSetClientBrainActive::handle)
                .add();
        INSTANCE.messageBuilder(CResetXPPacket.class, packetID++, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CResetXPPacket::encode)
                .decoder(CResetXPPacket::new)
                .consumerMainThread(CResetXPPacket::handle)
                .add();
    }

    public static void sendToServer(Object msg) {
        INSTANCE.send(PacketDistributor.SERVER.noArg(), msg);
    }

    public static void sendToPlayer(Object msg, ServerPlayer player) {
        Supplier<ServerPlayer> playerSupplier = () -> player;
        INSTANCE.send(PacketDistributor.PLAYER.with(playerSupplier), msg);
    }

    public static void sendToAllClients(Object msg) {
        INSTANCE.send(PacketDistributor.ALL.noArg(), msg);
    }

    public static void sendVanillaPacket(Object msg, ServerPlayer player) {
        if (player.getServer() == null)
            return;
        var connection = player.connection.connection;
        if (connection.isConnected()) {
            PacketSendListener listener = new PacketSendListener() {
                @Override
                public void onSuccess() {
                    PacketSendListener.super.onSuccess();
                }

                @Override
                public Packet<?> onFailure() {
                    return PacketSendListener.super.onFailure();
                }
            };
            connection.send((Packet<?>) msg, listener);
        }
    }

    public static void sendVanillaPacket(Object msg, ServerPlayer player, int delay) {
        if (player.getServer() == null)
            return;
//        player.getServer().getConnection().getConnections().forEach((e) -> {
        var connection = player.connection.connection;
        if (connection.isConnected()) {
            PacketSendListener listener = new PacketSendListener() {
                @Override
                public void onSuccess() {
                    PacketSendListener.super.onSuccess();
                }

                @Override
                public Packet<?> onFailure() {
                    return PacketSendListener.super.onFailure();
                }
            };

            ServerTimingHandler.scheduleEvent("PacketEvent", delay,
                    () -> connection.send((Packet<?>) msg, listener), true);
        }
    }
}
