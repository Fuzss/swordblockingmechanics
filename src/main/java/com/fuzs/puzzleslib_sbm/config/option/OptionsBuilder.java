package com.fuzs.puzzleslib_sbm.config.option;

import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

@SuppressWarnings({"UnusedReturnValue", "unused"})
public class OptionsBuilder {

    private final ForgeConfigSpec.Builder builder;
    private final ModConfig.Type type;
    private AbstractElement activeElement;
    private ConfigOption.ConfigOptionBuilder<?> activeOptionBuilder;

    public OptionsBuilder(ModConfig.Type type) {

        this.builder = new ForgeConfigSpec.Builder();
        this.type = type;
    }

    public OptionsBuilder comment(String... comment) {

        this.builder.comment(comment);
        return this;
    }

    public OptionsBuilder push(AbstractElement element) {

        assert this.activeElement == null : "Unable to push element on builder: " + "Element already set";
        this.activeElement = element;
        this.builder.push(element.getRegistryName().getPath());

        return this;
    }

    public OptionsBuilder push(String path) {

        this.tryCreate(null);
        this.builder.push(path);
        return this;
    }

    public OptionsBuilder pop(AbstractElement element) {

        assert element == this.activeElement : "Unable to pop element from builder: " + "No element set";

        this.pop();
        this.activeElement = null;

        return this;
    }

    public OptionsBuilder pop() {

        this.tryCreate(null);
        this.builder.pop();
        return this;
    }

    public ForgeConfigSpec build() {

        return this.builder.build();
    }

    public StringOption.StringOptionBuilder define(String optionName, String defaultValue) {

        return this.tryCreate(new StringOption.StringOptionBuilder(optionName, defaultValue));
    }

    public BooleanOption.BooleanOptionBuilder define(String optionName, boolean defaultValue) {

        return this.tryCreate(new BooleanOption.BooleanOptionBuilder(optionName, defaultValue));
    }

    public IntegerOption.IntegerOptionBuilder define(String optionName, int defaultValue) {

        return this.tryCreate(new IntegerOption.IntegerOptionBuilder(optionName, defaultValue));
    }

    public LongOption.LongOptionBuilder define(String optionName, long defaultValue) {

        return this.tryCreate(new LongOption.LongOptionBuilder(optionName, defaultValue));
    }

    public DoubleOption.DoubleOptionBuilder define(String optionName, double defaultValue) {

        return this.tryCreate(new DoubleOption.DoubleOptionBuilder(optionName, defaultValue));
    }

    public <T extends Enum<T>> EnumOption.EnumOptionBuilder<T> define(String optionName, T defaultValue) {

        return this.tryCreate(new EnumOption.EnumOptionBuilder<>(optionName, defaultValue));
    }

    @Nullable
    private <T extends ConfigOption.ConfigOptionBuilder<?>> T tryCreate(@Nullable T optionBuilder) {

        if (this.activeOptionBuilder != null) {

            this.create(this.activeOptionBuilder);
        }

        this.activeOptionBuilder = optionBuilder;
        return optionBuilder;
    }

    private <T> void create(ConfigOption.ConfigOptionBuilder<T> builder) {

        if (builder.comment.length != 0) {

            this.builder.comment(builder.comment);
        }

        BiFunction<ForgeConfigSpec.ConfigValue<T>, ModConfig.Type, ConfigOption<T>> factory = builder.getFactory();
        ForgeConfigSpec.ConfigValue<T> configValue = builder.getConfigValue(this.builder);
        ConfigOption<T> option = factory.apply(configValue, this.type);
        this.activeElement.addOption(option);
    }

}
