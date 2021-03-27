package com.fuzs.puzzleslib_sbm.config.implementation;

import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.BiFunction;

public class BooleanOption extends ConfigOption<Boolean> {

    BooleanOption(ForgeConfigSpec.ConfigValue<Boolean> value, ModConfig.Type type, BooleanOptionBuilder builder) {

        super(value, type, builder);
    }

    public static class BooleanOptionBuilder extends ConfigOptionBuilder<Boolean> {

        BooleanOptionBuilder(OptionsBuilder builder, String name, Boolean defaultValue) {

            super(builder, name, defaultValue);
        }

        @Override
        BiFunction<ForgeConfigSpec.ConfigValue<Boolean>, ModConfig.Type, ConfigOption<Boolean>> getFactory() {

            return (value, type) -> new BooleanOption(value, type, this);
        }

        @Override
        ForgeConfigSpec.ConfigValue<Boolean> getConfigValue(ForgeConfigSpec.Builder builder) {

            return builder.define(this.name, (boolean) this.defaultValue);
        }

    }

}
