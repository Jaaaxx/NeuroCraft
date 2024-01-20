package com.dementia.neurocraft.server;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.network.CAuditoryHallucinationPacket;
import com.dementia.neurocraft.network.CHallucinationListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import net.minecraft.client.resources.sounds.Sound;
import net.minecraft.core.Direction;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BowItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.util.ForgeSoundType;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;

import java.util.*;

import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;


@EventBusSubscriber(modid = NeuroCraft.MODID)
public class AuditoryHallucinations {
    static int c = 0;
    static Map<Player, List<Entity>> playerEntityMap = new HashMap<>();

    @SubscribeEvent
    public static void onServerTick(TickEvent.ServerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            if (c++ % (20 * 5) == 0) {
                spawnAuditoryHallucinations(event);
            }
        }
    }

    private static void spawnAuditoryHallucinations(TickEvent.ServerTickEvent event) {
        for (ServerPlayer p : event.getServer().getPlayerList().getPlayers()) {
            boolean spawnHallucination = (new Random().nextInt(500) < getPlayerSanity(p));

            if (spawnHallucination) {
                var pool = new net.minecraft.sounds.SoundEvent[]{
                        SoundEvents.ZOMBIE_AMBIENT,
                        SoundEvents.SKELETON_AMBIENT,
                        SoundEvents.CREEPER_PRIMED,
                        SoundEvents.WARDEN_ANGRY,
                        SoundEvents.GHAST_SCREAM};
                SoundEvent soundEvent = pool[new Random().nextInt(pool.length)];
                PacketHandler.sendToPlayer(new CAuditoryHallucinationPacket(soundEvent), p);
            }
        }
    }
}