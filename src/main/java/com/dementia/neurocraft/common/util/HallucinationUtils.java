package com.dementia.neurocraft.common.util;

import com.dementia.neurocraft.client.internal.SoundManager;
import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.CSetClientBrainActive;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.util.ModSoundEventsRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import static com.dementia.neurocraft.gui.Overlays.SanityHudOverlay.setGUIBrainActive;

public class HallucinationUtils {
    public static final int PEAK_SANITY = 1000;
    public static final int SCALING_INCREMENT = 1;

    public static void HallucinationOccured(Player player) {
        PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(ModSoundEventsRegistry.CONFUSED.get()), (ServerPlayer) player);
        PacketHandler.sendToPlayer(new CSetClientBrainActive(), (ServerPlayer) player);
    }
    public static void HallucinationOccured(Player player, boolean playSound, boolean showGUI) {
        if (playSound)
            PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(ModSoundEventsRegistry.CONFUSED.get()), (ServerPlayer) player);
        if (showGUI)
            PacketHandler.sendToPlayer(new CSetClientBrainActive(), (ServerPlayer) player);
    }

    public static void HallucinationOccuredClient() {
        if (com.dementia.neurocraft.config.ClientConfigs.HALLUCINATION_SFX.get())
            SoundManager.playSoundRandomPitchVolume(ModSoundEventsRegistry.CONFUSED.get());
        setGUIBrainActive();
    }
    public static void HallucinationOccuredClient(boolean playSound, boolean showGUI) {
        if (com.dementia.neurocraft.config.ClientConfigs.HALLUCINATION_SFX.get() && playSound)
            SoundManager.playSoundRandomPitchVolume(ModSoundEventsRegistry.CONFUSED.get());
        if (showGUI)
            setGUIBrainActive();
    }
}
