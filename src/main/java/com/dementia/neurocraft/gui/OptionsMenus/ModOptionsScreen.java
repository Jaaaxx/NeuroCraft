package com.dementia.neurocraft.gui.OptionsMenus;

import com.dementia.neurocraft.config.NewWorldConfigs;
import com.dementia.neurocraft.config.ServerConfigs;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvents;
import net.minecraftforge.common.ForgeConfigSpec;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

public class ModOptionsScreen extends Screen {
    protected final Screen lastScreen;
    protected OptionsList list;
    protected String lang_prefix;
    protected ArrayList<ForgeConfigSpec.ConfigValue> all_options = new ArrayList<>();
    private boolean newWorldConfig = false;


    public ModOptionsScreen(Screen lastScreen, Component title, String lang_prefix) {
        super(title);
        this.lastScreen = lastScreen;
        this.lang_prefix = lang_prefix;
    }

    protected void addSliders(Map<String, ForgeConfigSpec.ConfigValue<Integer>> options) {
        ArrayList<OptionInstance<Integer>> sliderOptions = new ArrayList<>();
        for (HashMap.Entry<String, ForgeConfigSpec.ConfigValue<Integer>> entry : options.entrySet()) {
            sliderOptions.add(createSliderFromConfig(entry.getKey(), entry.getValue()));
        }
        OptionInstance[] ret = new OptionInstance[sliderOptions.size()];
        ret = sliderOptions.toArray(ret);
        this.list.addSmall(ret);
    }

    protected void addBooleans(Map<String, ForgeConfigSpec.ConfigValue<Boolean>> options) {
        ArrayList<OptionInstance<Boolean>> sliderOptions = new ArrayList<>();
        for (HashMap.Entry<String, ForgeConfigSpec.ConfigValue<Boolean>> entry : options.entrySet()) {
            sliderOptions.add(createBooleanButtonFromConfig(entry.getKey(), entry.getValue()));
        }
        OptionInstance[] ret = new OptionInstance[sliderOptions.size()];
        ret = sliderOptions.toArray(ret);
        this.list.addSmall(ret);
    }

    protected OptionInstance<Integer> createSliderFromConfig(String name, ForgeConfigSpec.ConfigValue<Integer> config) {
        String disp_name = Component.translatable(this.lang_prefix + name).getString();
        String prefix = disp_name + ": ";
        String suffix = name.equals("SCALING_INTERVAL") ? " sec" : "";
        var range = this.newWorldConfig ? NewWorldConfigs.RANGES.get(config) : ServerConfigs.RANGES.get(config);
        return new OptionInstance<>(disp_name, OptionInstance.noTooltip(),
                (label, val) -> Component.literal(prefix + val + suffix),
                (new OptionInstance.IntRange(range.getKey(), range.getValue()))
                        .xmap((val) -> val, (val) -> val),
                Codec.intRange(range.getKey(), range.getValue()), config.get(), config::set);
    }

    protected void resetValuesToDefault() {
        for (var opt : this.all_options) {
            opt.set(opt.getDefault());
        }
        this.rebuildWidgets();
    }

    protected OptionInstance createBooleanButtonFromConfig(String name, ForgeConfigSpec.ConfigValue<Boolean> config) {
        List<String> bools = new ArrayList<>(config.get() ? Arrays.asList("true", "false") : Arrays.asList("false", "true"));
        name = Component.translatable(this.lang_prefix + name).getString();
        return new OptionInstance(name, OptionInstance.noTooltip(), (label, val) -> {
            return val.equals("") ? styleBool(bools.get(0)) : styleBool((String) val);
        }, new OptionInstance.LazyEnum(() -> {
            return bools.stream().toList();
        }, (p_232011_) -> {
            return Optional.of(p_232011_);
        }, Codec.STRING), "", (val) -> {
            Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(SoundEvents.UI_BUTTON_CLICK, 1.0F));
            config.set(val.equals("true"));
        });
    }

    protected Component styleBool(String textBool) {
        return textBool.equals("true")
                ? Component.literal(ChatFormatting.DARK_GREEN + textBool) :
                Component.literal(ChatFormatting.RED + textBool);
    }

    protected void addResetToDefaultsButton() {
        this.addRenderableWidget(Button.builder(Component.literal("Reset"), (s) -> this.resetValuesToDefault()).bounds(this.width - 70, this.height - 27, 60, 20).build());
    }

    protected void putAllConfigsInMenu(Class configClass) {
        if (configClass == NewWorldConfigs.class)
            this.newWorldConfig = true;

        List<Field> configFields = Stream.of(configClass.getFields())
                .filter(field -> ForgeConfigSpec.ConfigValue.class.isAssignableFrom(field.getType()))
                .toList();

        HashMap<String, ForgeConfigSpec.ConfigValue<Integer>> intOptions = new HashMap<>();
        HashMap<String, ForgeConfigSpec.ConfigValue<Boolean>> boolOptions = new HashMap<>();


        for (Field cf : configFields) {
            try {
                ForgeConfigSpec.ConfigValue option = (ForgeConfigSpec.ConfigValue) cf.get(null);
                if (option.get() instanceof Boolean) {
                    boolOptions.put(cf.getName(), (ForgeConfigSpec.ConfigValue<Boolean>) option);
                } else if (option.get() instanceof Integer) {
                    intOptions.put(cf.getName(), (ForgeConfigSpec.ConfigValue<Integer>) option);
                }
                all_options.add(option);
            } catch (IllegalAccessException ignored) {
            }
        }

        addSliders(intOptions);
        addBooleans(boolOptions);
    }

    protected static Component percentValueLabel(Component p_231898_, double p_231899_) {
        return Component.translatable("options.percent_value", new Object[]{p_231898_, (int) (p_231899_ * 100.0)});
    }

    public void render(@NotNull GuiGraphics graphics, int cursorX, int cursorY, float frameTime) {
        super.render(graphics, cursorX, cursorY, frameTime);
        graphics.drawCenteredString(this.font, this.title, this.width / 2, 15, 16777215);
    }
}
