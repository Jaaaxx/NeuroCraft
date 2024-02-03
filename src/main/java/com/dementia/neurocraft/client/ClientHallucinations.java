package com.dementia.neurocraft.client;

import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SRemoveHallucinationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.TickEvent.PlayerTickEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.dementia.neurocraft.common.Common.HallucinationOccuredClient;

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
                if (entity.distanceTo(p) >= 12) {
                    PacketHandler.sendToServer(new SRemoveHallucinationPacket(entity.getId()));
                    entity.remove(Entity.RemovalReason.DISCARDED);
                }
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