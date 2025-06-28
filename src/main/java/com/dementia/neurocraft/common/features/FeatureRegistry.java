package com.dementia.neurocraft.common.features;

import java.util.Arrays;
import java.util.List;

/**
 * Shared registry of all feature IDs that can be accessed from both client and server.
 * This prevents server configs from depending on client-only classes.
 */
public class FeatureRegistry {
    
    public static final List<String> CLIENT_FEATURE_IDS = Arrays.asList(
        "PSYCHOSIS",
        "ItemNameDementia", 
        "ItemTypeDementia",
        "RandomizeHealthBars",
        "RandomizeXP",
        "EnemyHallucination",
        "FOVChanges",
        "BrightnessChanges",
        "FramerateChanges", 
        "RenderDistanceChanges",
        "ControlSwaps",
        "PlayerDisorientation",
        "ClientMobSpawnRandomization"
    );
    
    public static final List<String> SERVER_FEATURE_IDS = Arrays.asList(
        "AuditoryHallucinations",
        "FakeBlockBreaking",
        "FakeBlockPlacing", 
        "EatingDisorder",
        "FurnaceUncooking",
        "InventoryDisarray",
        "OreHallucinationVein",
        "RandomTeleportBackwards",
        "EnemyHallucination" // Note: EnemyHallucination exists on both sides
    );
    
    /**
     * Get all feature IDs (both client and server)
     */
    public static List<String> getAllFeatureIds() {
        List<String> allIds = Arrays.asList(CLIENT_FEATURE_IDS.toArray(new String[0]));
        // Add server IDs that aren't already in client list
        SERVER_FEATURE_IDS.forEach(id -> {
            if (!allIds.contains(id)) {
                allIds.add(id);
            }
        });
        return allIds;
    }
} 