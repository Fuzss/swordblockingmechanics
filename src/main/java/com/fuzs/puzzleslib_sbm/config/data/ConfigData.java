package com.fuzs.puzzleslib_sbm.config.data;


import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * internal storage for registered config entries
 * @param <S> config value of a certain type
 * @param <T> type for value
 * @param <R> return type after applying transformer
 */
public class ConfigData<S extends ForgeConfigSpec.ConfigValue<T>, T, R> implements IConfigData<T, R> {

    /**
     * config value entry
     */
    private final S configValue;
    /**
     * config path for value
     */
    private final String path;
    /**
     * config type of this entry
     */
    private final ModConfig.Type type;
    /**
     * action to perform when the entry is updated
     */
    private final Consumer<R> syncToField;
    /**
     * transformation to apply when returning value, usually {@link Function#identity}
     */
    private final Function<T, R> transformValue;

    public ConfigData(S configValue, ModConfig.Type configType, Consumer<R> syncToField, Function<T, R> transformValue) {

        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.type = configType;
        this.syncToField = syncToField;
        this.transformValue = transformValue;
    }

    @Override
    public R get() {

        return this.transformValue.apply(this.getRaw());
    }

    @Override
    public T getRaw() {

        return this.configValue.get();
    }

    @Override
    public void sync() {

        this.syncToField.accept(this.get());
    }

    @Override
    public void modify(UnaryOperator<T> operator) {

        this.configValue.set(operator.apply(this.getRaw()));
        this.sync();
    }

    @Override
    public boolean isAtPath(String path) {

        return this.path.equals(path);
    }

    @Override
    public ModConfig.Type getType() {

        return this.type;
    }

}
