package com.dementia.neurocraft.network;

import com.dementia.neurocraft.config.ServerConfigs;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.network.CustomPayloadEvent;
import net.minecraftforge.fml.DistExecutor;

import java.util.HashMap;
import java.util.Map;

public class CConfigValueSyncPacket {
    private final Map<String, Object> configValues;

    public CConfigValueSyncPacket(Map<String, Object> configValues) {
        this.configValues = configValues;
    }

    public CConfigValueSyncPacket(FriendlyByteBuf buf) {
        this.configValues = new HashMap<>();
        int size = buf.readInt();
        for (int i = 0; i < size; i++) {
            String key = buf.readUtf();
            String type = buf.readUtf();
            Object value;
            switch (type) {
                case "Boolean" -> value = buf.readBoolean();
                case "Integer" -> value = buf.readInt();
                case "String" -> value = buf.readUtf();
                default -> throw new IllegalArgumentException("Unknown type: " + type);
            }
            configValues.put(key, value);
        }
    }

    public void encode(FriendlyByteBuf buf) {
        buf.writeInt(configValues.size());
        for (Map.Entry<String, Object> entry : configValues.entrySet()) {
            buf.writeUtf(entry.getKey());
            Object value = entry.getValue();
            if (value instanceof Boolean) {
                buf.writeUtf("Boolean");
                buf.writeBoolean((Boolean) value);
            } else if (value instanceof Integer) {
                buf.writeUtf("Integer");
                buf.writeInt((Integer) value);
            } else if (value instanceof String) {
                buf.writeUtf("String");
                buf.writeUtf((String) value);
            } else {
                throw new IllegalArgumentException("Unsupported type: " + value.getClass());
            }
        }
    }

    public void handle(CustomPayloadEvent.Context ctx) {
        if (!ctx.isClientSide()) {
            ctx.setPacketHandled(false);
            return;
        }

        ctx.enqueueWork(() -> {
            // Update client-side config values without saving to file
            for (Map.Entry<String, Object> entry : configValues.entrySet()) {
                String key = entry.getKey();
                Object value = entry.getValue();
                
                // Update feature configs
                var featureConfig = ServerConfigs.FEATURE_CONFIGS.get(key);
                if (featureConfig != null && value instanceof Boolean) {
                    featureConfig.set((Boolean) value);
                    continue;
                }
                
                // Update other configs
                switch (key) {
                    case "PLAYER_SCALING" -> {
                        if (value instanceof Boolean) ServerConfigs.PLAYER_SCALING.set((Boolean) value);
                    }
                    case "SANITY_RESET_UPON_DEATH" -> {
                        if (value instanceof Boolean) ServerConfigs.SANITY_RESET_UPON_DEATH.set((Boolean) value);
                    }
                    case "INITIAL_SANITY" -> {
                        if (value instanceof Integer) ServerConfigs.INITIAL_SANITY.set((Integer) value);
                    }
                    case "SCALING_INTERVAL" -> {
                        if (value instanceof Integer) ServerConfigs.SCALING_INTERVAL.set((Integer) value);
                    }
                }
            }
            
            // Trigger config spec reload to update any dependent systems
            ServerConfigs.SPEC.afterReload();
            
            // Refresh GUI if player is in config menu (client-side only)
            DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                ClientPacketHandler.handleConfigGUIRefresh();
            });
        });
        
        ctx.setPacketHandled(true);
    }
} 