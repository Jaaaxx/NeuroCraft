package com.dementia.neurocraft.commands;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureRegistry;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.CTriggerClientFeaturePacket;
import com.dementia.neurocraft.network.CUpdatePlayerSanityPacket;
import com.dementia.neurocraft.config.ConfigSyncHandler;
import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.server.internal.PlayerScalingManager.*;
import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;

@Mod.EventBusSubscriber(modid = MODID)
public final class FeatureCommandDispatcher {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

        // ===== SANITY CONTROL COMMANDS =====
        
        // /sanity get - Show current sanity
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

        // /sanity set <value> - Set sanity to specific value
        dispatcher.register(
                Commands.literal("sanity")
                        .then(Commands.literal("set")
                                .then(Commands.argument("value", IntegerArgumentType.integer(0, PEAK_SANITY))
                                        .requires(src -> src.getEntity() instanceof ServerPlayer)
                                        .executes(ctx -> {
                                            ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                            int value = IntegerArgumentType.getInteger(ctx, "value");
                                            setPlayerSanity(player, value);
                                            
                                            // Update client immediately
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

        // /sanity add <value> - Add/subtract from current sanity
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
                                            
                                            // Update client immediately
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

        // /sanity reset - Reset to initial sanity
        dispatcher.register(
                Commands.literal("sanity")
                        .then(Commands.literal("reset")
                                .requires(src -> src.getEntity() instanceof ServerPlayer)
                                .executes(ctx -> {
                                    ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                    giveInitialSanity(player);
                                    int newSanity = getPlayerSanity(player);
                                    
                                    // Update client immediately
                                    PacketHandler.sendToPlayer(new CUpdatePlayerSanityPacket(newSanity), player);
                                    
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "Reset sanity to initial value: " + newSanity + "/" + PEAK_SANITY
                                    ), false);
                                    return 1;
                                })
                        )
        );

        // ===== FEATURE COMMANDS (for force-triggering, not affecting sanity) =====

        // Add debug command for feature states
        dispatcher.register(
                Commands.literal("feature")
                        .then(Commands.literal("debug")
                                .requires(src -> src.getEntity() instanceof ServerPlayer)
                                .executes(ctx -> {
                                    ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                    
                                    int sanity = getPlayerSanity(player);
                                    ctx.getSource().sendSuccess(() -> Component.literal(
                                            "=== PLAYER SANITY: " + sanity + "/" + PEAK_SANITY + " (" + 
                                            String.format("%.1f%%", (sanity / (double) PEAK_SANITY) * 100) + ") ==="
                                    ), false);
                                    
                                    ctx.getSource().sendSuccess(() -> Component.literal("=== SERVER FEATURES ==="), false);
                                    for (Feature feature : ServerFeatureController.getFeatures()) {
                                        boolean canTrigger = feature.isEnabled() && sanity >= feature.getSanityThreshold();
                                        final String status = feature.isEnabled() 
                                            ? "ENABLED (sanity " + feature.getSanityThreshold() + "+ req: " + (canTrigger ? "MET" : "NOT MET") + ")"
                                            : "DISABLED";
                                        ctx.getSource().sendSuccess(() -> Component.literal(
                                            feature.getId() + " -> " + status
                                        ), false);
                                    }
                                    
                                    ctx.getSource().sendSuccess(() -> Component.literal("=== TRIGGERING CONFIG SYNC ==="), false);
                                    ConfigSyncHandler.syncFeatureStates();
                                    ctx.getSource().sendSuccess(() -> Component.literal("Config sync complete!"), false);
                                    
                                    return 1;
                                })
                        )
        );

        // Register commands for client features (triggered via packets)
        for (String featureId : FeatureRegistry.CLIENT_FEATURE_IDS) {
            String id = featureId.toLowerCase();

            dispatcher.register(
                    Commands.literal("feature")
                            .then(Commands.literal(id)
                                    .then(Commands.literal("run")
                                            .requires(src -> src.getEntity() instanceof ServerPlayer)
                                            .executes(ctx -> {
                                                ServerPlayer player = (ServerPlayer) ctx.getSource().getEntity();
                                                // Send packet to force-trigger client feature on the target player
                                                PacketHandler.sendToPlayer(new CTriggerClientFeaturePacket(featureId), player);
                                                ctx.getSource().sendSuccess(() -> Component.literal("Force-triggered feature '" + id + "'."), false);
                                                return 1;
                                            })
                                    )
                            )
            );
        }

        // Register commands for server features
        for (Feature feature : ServerFeatureController.getFeatures()) {
            if (!feature.supportsManualTrigger()) continue;

            String id = feature.getId().toLowerCase();

            dispatcher.register(
                    Commands.literal("feature")
                            .then(Commands.literal(id)
                                    .then(Commands.literal("run")
                                            .requires(src -> src.getEntity() instanceof ServerPlayer)
                                            .executes(ctx -> {
                                                // Force-trigger the feature regardless of sanity level
                                                feature.performServer((ServerPlayer) ctx.getSource().getEntity());
                                                ctx.getSource().sendSuccess(() -> Component.literal("Force-triggered feature '" + id + "'."), false);
                                                return 1;
                                            })
                                    )
                            )
            );
        }
    }
}
