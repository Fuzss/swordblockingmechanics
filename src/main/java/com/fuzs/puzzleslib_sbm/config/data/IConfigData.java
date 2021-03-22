package com.fuzs.puzzleslib_sbm.config.data;

import net.minecraftforge.fml.config.ModConfig;

import java.util.function.UnaryOperator;

public interface IConfigData<T, R> {

    /**
     * @return current value from entry
     */
    R get();

    /**
     * @return current raw value from entry
     */
    T getRaw();

    /**
     * get value from config value and supply it to consumer
     */
    void sync();

    /**
     * modify a config value so the config file is updated as well and sync afterwards
     * @param operator action to apply to config value
     */
    void modify(UnaryOperator<T> operator);

    /**
     * @param path path to compare to
     * @return does <code>path</code> match the path of this config value
     */
    boolean isAtPath(String path);

    /**
     * @return get this config type
     */
    ModConfig.Type getType();

    /**
     * @param type type to compare to
     * @return does <code>type</code> match this type
     */
    default boolean isType(ModConfig.Type type) {

        return this.getType() == type;
    }

}
