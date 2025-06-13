package com.dementia.neurocraft.common.features;

public enum FeatureTrigger {
    TICK,            // Primary (server/player) Tick-driven only
    CLIENT_TICK,
    BLOCK_BREAK,     // BlockEvent.BreakEvent
    BLOCK_PLACE,
    FINISH_USE_ITEM,
    CONTAINER_OPEN
    // Extend with more as needed
}
