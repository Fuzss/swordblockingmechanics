package com.fuzs.puzzleslib_sbm.config.implementation;

import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.function.BiFunction;

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

    public BooleanOption.BooleanOptionBuilder define(String name, boolean defaultValue) {

        return this.tryCreate(new BooleanOption.BooleanOptionBuilder(this, name, defaultValue));
    }

    public IntegerOption.IntegerOptionBuilder define(String name, int defaultValue) {

        return this.tryCreate(new IntegerOption.IntegerOptionBuilder(this, name, defaultValue));
    }

    @Nullable
    private <T extends ConfigOption.ConfigOptionBuilder<?>> T tryCreate(@Nullable T builder) {

        if (this.activeOptionBuilder != null) {

            this.create(this.activeOptionBuilder);
        }

        this.activeOptionBuilder = builder;
        return builder;
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
