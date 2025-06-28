package com.dementia.neurocraft.client.internal;

import com.mojang.authlib.GameProfile;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.entity.player.PlayerRenderer;
import net.minecraft.world.entity.LivingEntity;

import java.util.Map;
import java.util.UUID;
import java.util.WeakHashMap;

public final class EntityRandomizer {
    private static final Map<LivingEntity, GameProfile> PROFILES = new WeakHashMap<>();
    private static final Map<LivingEntity, HallucinationRenderHijack.DummyPlayer> DUMMIES = new WeakHashMap<>();
    private static boolean enabled = false;
    private static PlayerRenderer playerRenderer = null;

    static {
        HallucinationRenderHijack.apply();
    }

    public static void renderAllEntitiesAsRandomPlayers(boolean enable) {
        enabled = enable;
        if (!enable) {
            PROFILES.clear();
            DUMMIES.clear();
        }
    }
    public static void convertEntityIntoRandomPlayer(LivingEntity entity, boolean enable) {
        if (entity == null) return;
        if (enable) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), PlayerHallucinations.getRandomName());
            PROFILES.put(entity, profile);
            DUMMIES.remove(entity);
        } else {
            PROFILES.remove(entity);
            DUMMIES.remove(entity);
        }
    }

    public static boolean shouldRenderAsPlayer(LivingEntity entity) {
        return (enabled || PROFILES.containsKey(entity))
                && entity != Minecraft.getInstance().player;
    }

    public static HallucinationRenderHijack.DummyPlayer getOrCreateDummy(LivingEntity mob) {
        return DUMMIES.computeIfAbsent(mob, m -> {
            GameProfile gp = PROFILES.computeIfAbsent(m, ignored ->
                    new GameProfile(UUID.randomUUID(), PlayerHallucinations.getRandomName()));
            return new HallucinationRenderHijack.DummyPlayer((ClientLevel) m.level(), gp);
        });
    }

    public static PlayerRenderer getRenderer() {
        if (playerRenderer == null) {
            playerRenderer = (PlayerRenderer) Minecraft.getInstance()
                    .getEntityRenderDispatcher()
                    .getRenderer(Minecraft.getInstance().player);
        }
        return playerRenderer;
    }
}
