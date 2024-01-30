package com.dementia.neurocraft.client;

import com.dementia.neurocraft.NeuroCraft;
import com.dementia.neurocraft.network.CHallucinationListUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SRemoveHallucinationPacket;
import com.mojang.logging.LogUtils;
import it.unimi.dsi.fastutil.Hash;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.logging.Logger;

import static com.dementia.neurocraft.common.Common.HallucinationOccured;
import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;
import static com.dementia.neurocraft.server.PlayerScaling.getPlayerSanity;
import static com.mojang.text2speech.Narrator.LOGGER;

public class ClientHallucinations {
    static int c = 1;
    public static List<Entity> playerEntities = new ArrayList<>();
    static Map<Entity, Boolean> hallucinationsViewed = new HashMap<>();

    public static void onClientTick(PlayerTickEvent tick) {
        if (tick.phase == TickEvent.Phase.END) {

            c++;
            if (c == 5) {
                if (Minecraft.getInstance().level != null) {
                    checkPlayerHallucinationViewings();
                }
                c = 1;
            }
        }
    }


    public static void checkPlayerHallucinationViewings() {
        LocalPlayer p = Minecraft.getInstance().player;

        if (p == null)
            return;

        for (int i = playerEntities.size() - 1; i >= 0; --i) {
            Entity entity = playerEntities.get(i);
            if (entity instanceof LivingEntity) {
                boolean isInPlayerFOV = inPlayerFOV(p, entity);
                if (isInPlayerFOV && !getHallucinationViewed(entity)) {
                    setHallucinationViewed(entity, true);
                } else if (!isInPlayerFOV && getHallucinationViewed(entity) && !entity.isRemoved()) {
                    PacketHandler.sendToServer(new SRemoveHallucinationPacket(entity.getId()));
                    entity.remove(Entity.RemovalReason.DISCARDED);
                    HallucinationOccuredClient();
                }
            }
        }
    }


    private static boolean inPlayerFOV(Player player, Entity entity) {

        if (player == null) {
            return true;
        }

        if (entity.distanceTo(player) >= 7) {
            return false;
        }

        Vec3 visionVec = player.getLookAngle().normalize();
        Vec3 targetVec = new Vec3(entity.getX() - player.getX(),
                entity.getBoundingBox().minY + (double) (entity.getEyeHeight() / 2.0F) - (player.getY() + (double) player.getEyeHeight()),
                entity.getZ() - player.getZ());

        targetVec = targetVec.normalize();
        double dotProduct = visionVec.dot(targetVec);

        boolean inFOV = dotProduct > 0.1 && player.hasLineOfSight(entity);

        //https://forums.minecraftforge.net/topic/124315-how-to-get-the-entity-the-player-is-looking-at-1193/
        return inFOV;
    }


    public static boolean getHallucinationViewed(Entity entity) {
        var hallViewed = hallucinationsViewed.get(entity);
        if (hallViewed == null)
            return false;
        return hallViewed;
    }

    public static void setHallucinationViewed(Entity entity, boolean state) {
        hallucinationsViewed.put(entity, state);
    }
}