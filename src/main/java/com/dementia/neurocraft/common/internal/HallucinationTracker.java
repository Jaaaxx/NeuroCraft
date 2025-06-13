package com.dementia.neurocraft.common.internal;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

import java.util.*;

public final class HallucinationTracker {

    private Map<UUID, List<Integer>> hallucinations = new HashMap<>();

    /* SERVER-SIDE helpers */
    public void addHallucination(Player p, Entity e) {
        hallucinations.computeIfAbsent(p.getUUID(), __ -> new ArrayList<>())
                .add(e.getId());
    }
    public List<Integer> get(Player p) {
        return hallucinations.computeIfAbsent(p.getUUID(), __ -> new ArrayList<>());
    }
    public void clear(Player p) {
        hallucinations.remove(p.getUUID());
    }

    /* CLIENT-SIDE helpers */
    public void removeClientSide(int id) {
        hallucinations.values().forEach(list -> list.removeIf(i -> i == id));
    }


}
