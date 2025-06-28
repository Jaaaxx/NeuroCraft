package com.dementia.neurocraft.common.features;

import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;

import java.util.Random;

import static com.dementia.neurocraft.common.util.HallucinationUtils.PEAK_SANITY;

public abstract class Feature {
    protected static final Random RNG = new Random();

    private final String id;             // Unique identifier (for config / debugging)
    private final String displayName;
    private final int sanityThreshold;   // Minimum sanity required before feature is considered (1000 or PEAK_SANITY)
    private final double maxTriggerChance;  // Maximum probability (0.0 – 1.0) when at PEAK_SANITY
    private final int secondsInterval;      // Evaluate once every <interval> seconds
    private boolean enabled;             // Can be toggled at runtime
    private final FeatureTrigger triggerType;
    private final boolean canForceTrigger; // can be force triggered (by commands, etc.)

    protected Feature(String id, String displayName, int sanityThreshold, double maxTriggerChance, int secondsInterval, boolean enabled, FeatureTrigger triggerType, boolean canForceTrigger) {
        this.id = id;
        this.displayName = displayName;
        this.sanityThreshold = sanityThreshold;
        this.maxTriggerChance = maxTriggerChance;
        this.secondsInterval = secondsInterval;
        this.enabled = enabled;
        this.triggerType = triggerType;
        this.canForceTrigger = canForceTrigger;
    }

    public String getId() {
        return id;
    }

    public int getSanityThreshold() {
        return sanityThreshold;
    }

    public double getMaxTriggerChance() {
        return maxTriggerChance;
    }

    public String getDisplayName() {
        return displayName;
    }
    public int getSecondsInterval() {
        return secondsInterval;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public FeatureTrigger getTriggerType() {
        return triggerType;
    }

    public void performClient(Minecraft mc) { /* default no-op */ }

    public void performServer(ServerPlayer player) { /* default no-op */ }

    public final void tryRunClient(Minecraft mc, int sanity) {
        if (enabled && sanity >= sanityThreshold && passChance(sanity) && passInterval(mc.player.tickCount))
            performClient(mc);
    }

    public final void tryRunServer(ServerPlayer sp, int sanity) {
        if (enabled && sanity >= sanityThreshold && passChance(sanity) && passInterval(sp.tickCount))
            performServer(sp);
    }

    public boolean supportsManualTrigger() {
        return canForceTrigger;
    }

    /* shared helpers */
    private boolean passInterval(int tickCount) {
        int intervalTicks = secondsInterval * 20;

        if (intervalTicks <= 0) return true;

        return tickCount % intervalTicks == 0;
    }
    private boolean passChance(int sanity) {
        var db = RNG.nextDouble();
        return db <= maxTriggerChance * sanity / (double) PEAK_SANITY;
    }
}