package com.fuzs.puzzleslib_sbm.config;


import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nullable;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * internal storage for registered config entries
 * @param <S> config value of a certain type
 * @param <T> type for value
 * @param <R> return type after applying transformer
 */
public class ConfigValueData<S extends ForgeConfigSpec.ConfigValue<T>, T, R> {

    /**
     * config value entry
     */
    private final S value;
    /**
     * config path for value
     */
    private final String path;
    /**
     * full comment inside of commented config
     */
    @Nullable
    private String comment;
    /**
     * config type of this entry
     */
    final ModConfig.Type type;
    /**
     * action to perform when the entry is updated
     */
    private final Consumer<R> sync;
    /**
     * transformation to apply when returning value, usually {@link Function#identity}
     */
    private final Function<T, R> transformer;

    /**
     * new entry storage
     */
    ConfigValueData(S value, ModConfig.Type type, Consumer<R> sync, Function<T, R> transformer) {

        this.value = value;
        this.path = String.join(".", value.getPath());
        this.type = type;
        this.sync = sync;
        this.transformer = transformer;
    }

    /**
     * @return current value from entry
     */
    public R getValue() {

        return this.transformer.apply(this.getRawValue());
    }

    /**
     * @return current raw value from entry
     */
    T getRawValue() {

        return this.value.get();
    }

    /**
     * get value from config value and supply it to consumer
     */
    void sync() {

        this.sync.accept(this.getValue());
    }

    /**
     * modify a config value so the config file is updated as well and sync afterwards
     * @param operator action to apply to config value
     */
    public void modifyConfigValue(UnaryOperator<T> operator) {

        this.value.set(operator.apply(this.getRawValue()));
        this.sync();
    }

    /**
     * @param path path to compare to
     * @return does <code>path</code> match the path of this config value
     */
    boolean isAtPath(String path) {

        return this.path.equals(path);
    }

    String getComment(ForgeConfigSpec spec) {

        if (this.comment == null) {

            Object valueAtPath = spec.getRaw(this.value.getPath());
            if (valueAtPath instanceof ForgeConfigSpec.ValueSpec) {

                this.comment = ((ForgeConfigSpec.ValueSpec) valueAtPath).getComment();
            }
        }

        if (this.comment == null) {

            PuzzlesLib.LOGGER.error("Unable to get config comment at path \"" + this.path + "\": " + "No comment found");
        }

        return this.comment;
    }

}
