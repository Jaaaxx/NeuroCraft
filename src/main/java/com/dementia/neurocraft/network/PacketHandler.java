package com.dementia.neurocraft.network;

import com.dementia.neurocraft.NeuroCraft;
import net.minecraft.network.PacketSendListener;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundBlockUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundBossEventPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.BossEvent;
import net.minecraft.world.entity.player.Player;
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
        INSTANCE.messageBuilder(SRefreshClientBlockList.class, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SRefreshClientBlockList::encode)
                .decoder(SRefreshClientBlockList::new)
                .consumerMainThread(SRefreshClientBlockList::handle)
                .add();
        INSTANCE.messageBuilder(CHallBlockListUpdatePacket.class, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(CHallBlockListUpdatePacket::encode)
                .decoder(CHallBlockListUpdatePacket::new)
                .consumerMainThread(CHallBlockListUpdatePacket::handle)
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

    public static void sendVanillaPacket(Object msg, ServerPlayer player) {
        if (player.getServer() == null)
            return;
        var connection = player.connection.getConnection();
        if (connection.isConnected()) {
            PacketSendListener listener = new PacketSendListener() {
                @Override
                public void onSuccess() {
                    PacketSendListener.super.onSuccess();
                    System.out.println("Success packet sent");
                }

                @Override
                public Packet<?> onFailure() {
                    System.out.println("Failed packet sent");
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
        var connection = player.connection.getConnection();
        if (connection.isConnected()) {
            PacketSendListener listener = new PacketSendListener() {
                @Override
                public void onSuccess() {
                    PacketSendListener.super.onSuccess();
                    System.out.println("Success packet sent");
                }

                @Override
                public Packet<?> onFailure() {
                    System.out.println("Failed packet sent");
                    return PacketSendListener.super.onFailure();
                }
            };
            new Thread(() -> {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException exception) {
                    throw new RuntimeException(exception);
                }
                connection.send((Packet<?>) msg, listener);
            }).start();
        }
    }
}
