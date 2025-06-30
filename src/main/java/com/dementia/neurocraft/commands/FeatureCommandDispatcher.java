package com.dementia.neurocraft.commands;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.network.CUpdatePlayerSanityPacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;
import static com.dementia.neurocraft.server.internal.PlayerScalingManager.*;
import static com.dementia.neurocraft.server.internal.PlayerScalingManager.getPlayerSanity;

@Mod.EventBusSubscriber(modid = MODID)
public final class FeatureCommandDispatcher {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        for (Feature feature : ClientFeatureController.getFeatures()) {
            if (!feature.supportsManualTrigger()) continue;

            String id = feature.getId().toLowerCase();

            dispatcher.register(
                    Commands.literal("feature")
                            .then(Commands.literal(id)
                                    .then(Commands.literal("run")
                                            .requires(src -> src.getEntity() instanceof ServerPlayer)
                                            .executes(ctx -> {
                                                var mc = net.minecraft.client.Minecraft.getInstance();
                                                feature.performClient(Minecraft.getInstance());
                                                ctx.getSource().sendSuccess(() -> Component.literal("Feature '" + id + "' triggered."), false);
                                                return 1;
                                            })
                                    )
                            )
            );
        }
        for (Feature feature : ServerFeatureController.getFeatures()) {
            if (!feature.supportsManualTrigger()) continue;

            String id = feature.getId().toLowerCase();

            dispatcher.register(
                    Commands.literal("feature")
                            .then(Commands.literal(id)
                                    .then(Commands.literal("run")
                                            .requires(src -> src.getEntity() instanceof ServerPlayer)
                                            .executes(ctx -> {
                                                feature.performServer((ServerPlayer) ctx.getSource().getEntity());
                                                ctx.getSource().sendSuccess(() -> Component.literal("Feature '" + id + "' triggered."), false);
                                                return 1;
                                            })
                                    )
                            )
            );
        }

        dispatcher.register(
                Commands.literal("sanity")
                        .then(Commands.literal("get")
                                .requires(src -> src.getEntity() instanceof ServerPlayer)
                                .executes(ctx -> {
                                    ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                    int sanity = getPlayerSanity(player);
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "Current sanity: " + sanity + "/" + PEAK_SANITY + " (" +
                                                    String.format("%.1f%%", (sanity / (double) PEAK_SANITY) * 100) + ")"
                                    ), false);
                                    return 1;
                                })
                        )
        );

        dispatcher.register(
                Commands.literal("sanity")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, PEAK_SANITY))
                                        .requires(src -> src.getEntity() instanceof ServerPlayer)
                                        .executes(ctx -> {
                                            ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            setPlayerSanity(player, value);

                                            PacketHandler.sendToPlayer(new CUpdatePlayerSanityPacket(value), player);

                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                    "Set sanity to: " + value + "/" + PEAK_SANITY + " (" +
                                                            String.format("%.1f%%", (value / (double) PEAK_SANITY) * 100) + ")"
                                            ), false);
                                            return 1;
                                        })
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("sanity")
                        .then(Commands.literal("add")
                                .then(Commands.argument("value", IntegerArgumentType.integer(-PEAK_SANITY, PEAK_SANITY))
                                        .requires(src -> src.getEntity() instanceof ServerPlayer)
                                        .executes(ctx -> {
                                            ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                            int change = IntegerArgumentType.getInteger(ctx, "value");
                                            int currentSanity = getPlayerSanity(player);
                                            int newSanity = Math.max(0, Math.min(PEAK_SANITY, currentSanity + change));

                                            setPlayerSanity(player, newSanity);

                                            PacketHandler.sendToPlayer(new CUpdatePlayerSanityPacket(newSanity), player);

                                            String changeText = change >= 0 ? "+" + change : String.valueOf(change);
                                            ctx.getSource().sendSuccess(() -> Component.literal(
                                                    "Sanity " + currentSanity + " " + changeText + " = " + newSanity + "/" + PEAK_SANITY +
                                                            " (" + String.format("%.1f%%", (newSanity / (double) PEAK_SANITY) * 100) + ")"
                                            ), false);
                                            return 1;
                                        })
                                )
                        )
        );

        dispatcher.register(
                Commands.literal("sanity")
                        .then(Commands.literal("reset")
                                .requires(src -> src.getEntity() instanceof ServerPlayer)
                                .executes(ctx -> {
                                    ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                    giveInitialSanity(player);
                                    int newSanity = getPlayerSanity(player);

                                    PacketHandler.sendToPlayer(new CUpdatePlayerSanityPacket(newSanity), player);

                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "Reset sanity to initial value: " + newSanity + "/" + PEAK_SANITY
                                    ), false);
                                    return 1;
                                })
                        )
        );
    }


}
