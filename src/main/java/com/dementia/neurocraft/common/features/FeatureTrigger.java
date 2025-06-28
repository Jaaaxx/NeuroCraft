package com.dementia.neurocraft.common.features;

public enum FeatureTrigger {
    // Global
    TICK,

    // Client-side
    CLIENT_MOB_SPAWN,
    CLIENT_TICK,

    // Server-side
    SERVER_FINISH_USE_ITEM,
    SERVER_CONTAINER_OPEN,
    SERVER_BLOCK_BREAK,
    SERVER_BLOCK_PLACE;
}