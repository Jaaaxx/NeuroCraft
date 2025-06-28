package com.dementia.neurocraft.commands;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureRegistry;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.CTriggerClientFeaturePacket;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import static com.dementia.neurocraft.Neurocraft.MODID;

@Mod.EventBusSubscriber(modid = MODID)
public final class FeatureCommandDispatcher {

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        CommandDispatcher<CommandSourceStack> dispatcher = event.getDispatcher();

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
                                                // Send packet to trigger client feature on the target player
                                                PacketHandler.sendToPlayer(new CTriggerClientFeaturePacket(featureId), player);
                                                ctx.getSource().sendSuccess(() -> Component.literal("Feature '" + id + "' triggered."), false);
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
                                                feature.performServer((ServerPlayer) ctx.getSource().getEntity());
                                                ctx.getSource().sendSuccess(() -> Component.literal("Feature '" + id + "' triggered."), false);
                                                return 1;
                                            })
                                    )
                            )
            );
        }
    }
}
