package com.dementia.neurocraft.util;

import com.dementia.neurocraft.Neurocraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;

public class ModSoundEventsRegistry {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(ForgeRegistries.SOUND_EVENTS, Neurocraft.MODID);

    public static final RegistryObject<SoundEvent> CONFUSED = registerSoundEvent("confused");
    public static final RegistryObject<SoundEvent> STATICSWITCH = registerSoundEvent("staticswitch");
    public static final RegistryObject<SoundEvent> UNCANNY1 = registerSoundEvent("uncanny1");
    public static final RegistryObject<SoundEvent> UNCANNY2 = registerSoundEvent("uncanny2");
    public static final RegistryObject<SoundEvent> UNCANNY3 = registerSoundEvent("uncanny3");
    public static final RegistryObject<SoundEvent> UNCANNY4 = registerSoundEvent("uncanny4");
    public static final RegistryObject<SoundEvent> INVALID = registerSoundEvent("invalid");

    public static final ArrayList<RegistryObject<SoundEvent>> schitzoMusicOptions =
            new ArrayList<>(){{add(UNCANNY1); add(UNCANNY2); add(UNCANNY3); add(UNCANNY4);}};

    private static RegistryObject<SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () ->  SoundEvent.createVariableRangeEvent(new ResourceLocation(Neurocraft.MODID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
