package com.dementia.neurocraft.client.features.impl;

import com.dementia.neurocraft.common.features.Feature;
import com.dementia.neurocraft.common.features.FeatureTrigger;
import com.dementia.neurocraft.network.PacketHandler;
import com.dementia.neurocraft.network.SRemoveHallucinationPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;

import java.util.*;

import static com.dementia.neurocraft.Neurocraft.MODID;
import static com.dementia.neurocraft.common.util.HallucinationUtils.HallucinationOccuredClient;

// todo fix
@Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT)
public final class EnemyHallucination extends Feature {

    private final Set<Integer> hallucinationIds = new HashSet<>();
    private final Map<Integer, Boolean> viewed = new HashMap<>();
    private int tickCounter = 0;

    public EnemyHallucination() {
        super("ENEMY_HALLUCINATIONS", "Mob Hallucinations", 300, 0.3, 5, true, FeatureTrigger.TICK, false);
    }

    @Override
    public void performClient(Minecraft mc) {
        tickCounter++;
        if (tickCounter >= 5) {
            tickCounter = 0;
            checkHallucinationViewings(mc);
        }
    }

    private void checkHallucinationViewings(Minecraft mc) {
        LocalPlayer player = mc.player;
        if (player == null) return;

        Iterator<Integer> iterator = hallucinationIds.iterator();
        while (iterator.hasNext()) {
            int id = iterator.next();
            Entity entity = player.level().getEntity(id);

            if (!(entity instanceof LivingEntity)) {
                iterator.remove();
                viewed.remove(id);
                continue;
            }

            if (entity.distanceTo(player) >= 12) {
                discard(entity, id);
                iterator.remove();
                viewed.remove(id);
                continue;
            }

            boolean inFov = inPlayerFOV(player, entity);
            if (inFov && !viewed.getOrDefault(id, false)) {
                viewed.put(id, true);
            } else if (!inFov && viewed.getOrDefault(id, false) && !entity.isRemoved()) {
                discard(entity, id);
                iterator.remove();
                viewed.remove(id);
                HallucinationOccuredClient();
            }
        }
    }

    private boolean inPlayerFOV(Player player, Entity entity) {
        Vec3 visionVec = player.getLookAngle().normalize();
        Vec3 toTarget = new Vec3(
                entity.getX() - player.getX(),
                entity.getBoundingBox().minY + (entity.getEyeHeight() / 2.0F) - (player.getY() + player.getEyeHeight()),
                entity.getZ() - player.getZ()
        ).normalize();

        double dot = visionVec.dot(toTarget);
        return dot > 0.1 && player.hasLineOfSight(entity);
    }

    private void discard(Entity entity, int id) {
        PacketHandler.sendToServer(new SRemoveHallucinationPacket(id));
        entity.remove(Entity.RemovalReason.DISCARDED);
    }

    public void syncHallucinations(List<Entity> newHallucinations) {
        hallucinationIds.clear();
        viewed.clear();
        for (Entity e : newHallucinations) {
            hallucinationIds.add(e.getId());
            viewed.put(e.getId(), false);
        }
    }
}
