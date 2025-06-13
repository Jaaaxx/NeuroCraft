package com.dementia.neurocraft.gui.OptionsMenus;

import com.dementia.neurocraft.config.ConfigSyncHandler;
import com.dementia.neurocraft.config.ServerConfigs;
import com.dementia.neurocraft.network.CFeatureToggleUpdatePacket;
import com.dementia.neurocraft.network.PacketHandler;
import com.mojang.serialization.Codec;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.OptionInstance;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.OptionsList;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.event.config.ModConfigEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Stream;

public class ModOptionsScreen extends Screen {
    protected final Screen lastScreen;
    protected OptionsList list;
    protected String lang_prefix;
    protected ArrayList<ForgeConfigSpec.ConfigValue> all_options = new ArrayList<>();


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
        var range = ServerConfigs.RANGES.get(config);
        int currentVal = config.get();

        return new OptionInstance<>(
                disp_name,
                OptionInstance.noTooltip(),
                (label, val) -> Component.literal(prefix + val + suffix),
                new OptionInstance.IntRange(range.getKey(), range.getValue()).xmap(v -> v, v -> v),
                Codec.intRange(range.getKey(), range.getValue()),
                currentVal,
                val -> {
                    config.set(val);
                    if (ServerConfigs.modConfig != null) {
                        ServerConfigs.modConfig.save();
                        ConfigSyncHandler.syncFeatureStates();
                        ServerConfigs.SPEC.afterReload();
                    }
                }
        );
    }

    protected void resetValuesToDefault() {
        for (var opt : this.all_options) {
            opt.set(opt.getDefault());
        }
        this.rebuildWidgets();
    }

    protected OptionInstance<Boolean> createBooleanButtonFromConfig(String name, ForgeConfigSpec.ConfigValue<Boolean> config) {
        String dispName = Component.translatable(this.lang_prefix + name).getString();
        String current = config.get() ? "true" : "false";

        return new OptionInstance<>(
                dispName,
                OptionInstance.noTooltip(),
                (lbl, val) -> styleBool((String) val),
                new OptionInstance.LazyEnum(() -> List.of("true", "false"), Optional::of, Codec.STRING),
                current,
                val -> {
                    boolean b = "true".equals(val);
                    config.set(b);

                    if (ServerConfigs.modConfig != null) {
                        ServerConfigs.modConfig.save();
                        FMLJavaModLoadingContext
                                .get()
                                .getModEventBus()
                                .post(new ModConfigEvent.Reloading(ServerConfigs.modConfig));
                    }

                    if (FMLEnvironment.dist.isClient()) {
                        var conn = Minecraft.getInstance().getConnection();
                        if (conn != null && !Minecraft.getInstance().isLocalServer()) {
                            PacketHandler.sendToServer(
                                    new CFeatureToggleUpdatePacket(config.getPath().get(0), b)
                            );
                        }
                    }
                }
        );
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
        List<Field> configFields = Stream.of(configClass.getFields())
                .filter(field -> ForgeConfigSpec.ConfigValue.class.isAssignableFrom(field.getType()))
                .toList();

        HashMap<String, ForgeConfigSpec.ConfigValue<Integer>> intOptions = new HashMap<>();
        HashMap<String, ForgeConfigSpec.ConfigValue<Boolean>> boolOptions = new HashMap<>();


        for (Field cf : configFields) {
            try {
                ForgeConfigSpec.ConfigValue option = (ForgeConfigSpec.ConfigValue) cf.get(null);
                Object defaultVal = option.getDefault();
                if (defaultVal instanceof Boolean) {
                    boolOptions.put(cf.getName(), (ForgeConfigSpec.ConfigValue<Boolean>) option);
                } else if (defaultVal instanceof Integer) {
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
