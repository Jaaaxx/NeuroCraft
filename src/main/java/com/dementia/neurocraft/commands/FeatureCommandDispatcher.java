package com.dementia.neurocraft.commands;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.client.features.ClientFeatureController;
import com.dementia.neurocraft.server.features.ServerFeatureController;
import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.client.Minecraft;
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
    }
}
