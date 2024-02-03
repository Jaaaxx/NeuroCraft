package com.dementia.neurocraft.common;

import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.util.ModSoundEventsRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.dementia.neurocraft.EnabledFeatures.HALLUCINATION_SFX;

public class Common {
    public static void HallucinationOccured(Player player) {
        if (HALLUCINATION_SFX)
            PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(ModSoundEventsRegistry.CONFUSED.get()), (ServerPlayer) player);
    }

    public static void HallucinationOccuredClient() {
        if (HALLUCINATION_SFX)
            ClientSoundManager.playSoundRandomPitchVolume(ModSoundEventsRegistry.CONFUSED.get());
    }
}
