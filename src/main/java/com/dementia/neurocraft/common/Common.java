package com.dementia.neurocraft.common;

import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.util.ModSoundEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class Common {
    public static void HallucinationOccured(Player player) {
        PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(ModSoundEvents.CONFUSED.get()), (ServerPlayer) player);
    }
    public static void HallucinationOccuredClient() {
        var player = Minecraft.getInstance().player;
        assert player != null;
        player.playSound(ModSoundEvents.CONFUSED.get(), (float) (Math.random() * 2.9 + 0.1), (float) (Math.random() * 4.5 + 0.5));
    }
}
