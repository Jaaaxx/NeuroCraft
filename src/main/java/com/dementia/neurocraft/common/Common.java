package com.dementia.neurocraft.common;

import ca.weblite.objc.Client;
import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.util.ModSoundEventsRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.dementia.neurocraft.NeuroCraft.ClientModEvents.clientSoundManager;

public class Common {
    public static void HallucinationOccured(Player player) {
        PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(ModSoundEventsRegistry.CONFUSED.get()), (ServerPlayer) player);
    }

    public static void HallucinationOccuredClient() {
        ClientSoundManager.playSound(ModSoundEventsRegistry.CONFUSED.get());
    }
}
