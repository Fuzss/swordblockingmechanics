package com.fuzs.puzzleslib_sbm.config;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.client.config.BooleanConfigOption;
import com.fuzs.puzzleslib_sbm.client.config.ConfigOption;
import com.fuzs.puzzleslib_sbm.client.config.EnumConfigOption;
import com.fuzs.puzzleslib_sbm.config.data.ConfigData;
import com.fuzs.puzzleslib_sbm.config.data.IConfigData;
import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import com.fuzs.puzzleslib_sbm.element.ElementRegistry;
import com.fuzs.puzzleslib_sbm.util.INamespaceLocator;
import com.google.common.collect.*;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;
import net.minecraftforge.registries.IForgeRegistryEntry;
import org.apache.commons.lang3.tuple.Pair;

import javax.annotation.Nullable;
import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * main config manager for this mod
 */
@SuppressWarnings("unused")
public class ConfigManager implements INamespaceLocator {

    /**
     * singleton instance
     */
    private static ConfigManager instance;

    /**
     * config build helpers for each mod separately since they store the forge builders and specs
     */
    private final Map<String, ConfigBuilder> configBuilders = Maps.newHashMap();
    /**
     * all config entries as a set
     */
    private final Multimap<AbstractElement, IConfigData<?, ?>> configData = HashMultimap.create();

    /**
     * this class is a singleton
     */
    private ConfigManager() {

    }

    /**
     * register configs from non-empty builders and add listener from active mod container to {@link #onModConfig}
     * @param path optional directory inside of main config dir
     */
    public void load(String... path) {

        if (path.length > 0) {

            this.getBuilder().moveToFolder(path);
        }

        this.getBuilder().registerConfigs(ModLoadingContext.get());
        FMLJavaModLoadingContext.get().getModEventBus().addListener(this::onModConfig);
    }

    /**
     * fires on both loading and reloading, loading phase is required for initial setup
     * @param evt event provided by Forge
     */
    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        String modid = evt.getConfig().getModId();
        ModConfig.Type type = evt.getConfig().getType();
        if (this.getBuilder(modid).isSpecNotValid(type)) {

            PuzzlesLib.LOGGER.error("Unable to get values from " + type.extension() + " config for " + modid + " during " + (evt instanceof ModConfig.Loading ? "loading" : "reloading") + " phase: " + "Config spec not present");
        } else {

            if (this.syncAll(modid, type) && evt instanceof ModConfig.Reloading) {

                PuzzlesLib.LOGGER.info("Reloaded " + type.extension() + " config for mod " + modid);
            }
        }
    }

    /**
     * sync all config entries and notify all listeners
     * @param type config type for this listener
     */
    public void syncAll(ModConfig.Type type) {

        if (this.syncAll(null, type)) {

            PuzzlesLib.LOGGER.info("Reloaded " + type.extension() + " config for all mods");
        }
    }

    /**
     * sync config entries for specific type of config
     * call listeners for type as the config has somehow been loaded
     * @param modId mod to get entries for
     * @param type config type for this listener
     * @return was any data found for syncing
     */
    private boolean syncAll(@Nullable String modId, ModConfig.Type type) {

        Collection<IConfigData<?, ?>> data = this.getAllConfigData(modId, type, true);
        if (!data.isEmpty()) {

            data.forEach(IConfigData::sync);
            this.reloadListeners.stream()
                    .filter(listener -> listener.isModId(modId))
                    .filter(listener -> listener.isType(type))
                    .forEach(ReloadListener::run);

            return true;
        }

        return false;
    }

    /**
     * @param modId mod to get entries for
     * @param type config type for this listener
     * @return collection of enabled entries only for this mod and type
     */
    public Collection<IConfigData<?, ?>> getAllConfigData(@Nullable String modId, ModConfig.Type type, boolean onlyEnabled) {

        return this.configData.entries().stream()
                .filter(entry -> modId == null || entry.getKey().getRegistryName().getNamespace().equals(modId))
                .filter(entry -> !onlyEnabled || entry.getKey().isEnabled())
                .map(Map.Entry::getValue)
                .filter(data -> data.isType(type))
                .collect(Collectors.toSet());
    }

    public Multimap<AbstractElement, ConfigOption<?, ?, ?>> getAllConfigOptions(String modId) {

        Multimap<AbstractElement, ConfigOption<?, ?, ?>> modMap = TreeMultimap.create((key1, key2) -> {

            return key1.getDisplayName().compareToIgnoreCase(key2.getDisplayName());
        }, (value1, value2) -> {

            return value1.getBaseMessageTranslation().getString().compareToIgnoreCase(value2.getBaseMessageTranslation().getString());
        });
        for (Map.Entry<AbstractElement, IConfigData<?, ?>> entry : this.configData.entries()) {

            if (entry.getKey().getRegistryName().getNamespace().equals(modId)) {

                if (entry.getValue() instanceof ConfigOption<?, ?, ?>) {

                    modMap.put(entry.getKey(), (ConfigOption<?, ?, ?>) entry.getValue());
                }
            }
        }

        return modMap;
    }

    /**
     * register config entry for active type
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T> void registerEntry(S entry, Consumer<T> action) {

        this.registerEntry(entry, action, Function.identity());
    }

    /**
     * register config entry for active type
     * @param entry source config value object
     * @param action action to perform when value changes (is reloaded)
     * @param transformer transformation to apply when returning value
     * @param <S> config value of a certain type
     * @param <T> type for value
     * @param <R> final return type of config entry
     */
    public <S extends ForgeConfigSpec.ConfigValue<T>, T, R> void registerEntry(S entry, Consumer<R> action, Function<T, R> transformer) {

        ConfigBuilder builder = this.getBuilder();
        Pair<AbstractElement, ModConfig.Type> activeTuple = builder.getActiveTuple();
        if (activeTuple == null) {

            PuzzlesLib.LOGGER.error("Unable to register config entry: " + "Active builder is null");
        } else if (builder.isSpecNotBuilt(activeTuple.getRight())) {

            this.configData.put(activeTuple.getLeft(), create(entry, activeTuple.getRight(), action, transformer));
        } else {

            PuzzlesLib.LOGGER.error("Unable to register config entry: " + "Config spec already built");
        }
    }

    /**
     * add a listener for when the config is somehow loaded
     * @param element parent element for mod id
     * @param type config type for this listener
     * @param onReload listener to add
     */
    public void addListener(AbstractElement element, ModConfig.Type type, Runnable onReload) {

        this.reloadListeners.add(new ReloadListener(element.getRegistryName().getNamespace(), type, onReload));
    }

    /**
     * add a listener for when the config is somehow loaded
     * @param modId parent mod id
     * @param type config type for this listener
     * @param onReload listener to add
     */
    public void addListener(String modId, ModConfig.Type type, Runnable onReload) {

        this.reloadListeners.add(new ReloadListener(modId, type, onReload));
    }

    /**
     * @param type type of config
     * @param modId modid this config belongs to
     * @return config name as if it were generated by Forge itself
     */
    public static String getConfigName(String modId, ModConfig.Type type) {

        return String.format("%s-%s.toml", modId, type.extension());
    }

    /**
     * put config into it's own folder when there are multiples
     * @param type type of config
     * @param modId modid this config belongs to
     * @return name lead by folder
     */
    public static String getConfigNameInFolder(String modId, ModConfig.Type type) {

        return modId + File.separator + getConfigName(modId, type);
    }

    /**
     * @param entries entries to convert to string
     * @param <T> registry element type
     * @return entries as string list
     */
    @SafeVarargs
    public static <T extends IForgeRegistryEntry<T>> List<String> getKeyList(T... entries) {

        return Stream.of(entries)
                .map(IForgeRegistryEntry::getRegistryName)
                .filter(Objects::nonNull)
                .map(ResourceLocation::toString)
                .collect(Collectors.toList());
    }

    private static <S extends ForgeConfigSpec.ConfigValue<T>, T, R> IConfigData<?, ?> create(S configValue, ModConfig.Type configType, Consumer<R> syncToField, Function<T, R> transformValue) {

        if (FMLEnvironment.dist.isDedicatedServer()) {

            return new ConfigData<>(configValue, configType, syncToField, transformValue);
        }

        if (configValue instanceof ForgeConfigSpec.EnumValue<?>) {

            return new EnumConfigOption((ForgeConfigSpec.EnumValue) configValue, configType, syncToField, transformValue);
        } else if (configValue instanceof ForgeConfigSpec.BooleanValue) {

            return new BooleanConfigOption((ForgeConfigSpec.BooleanValue) configValue, configType, (Consumer<Boolean>) syncToField, (Function<Boolean, Boolean>) transformValue);
        }

        return new ConfigData<>(configValue, configType, syncToField, transformValue);
    }

    /**
     * get builder for active mod, create if not present
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder() {

        return this.getBuilder(this.getActiveNamespace());
    }

    /**
     * get builder for a given mod, create if not present
     * @param modid modid to get builder for
     * @return builder for active mod
     */
    private ConfigBuilder getBuilder(String modid) {

        return this.configBuilders.computeIfAbsent(modid, key -> new ConfigBuilder());
    }

    /**
     * @return instance of this
     */
    public static ConfigManager get() {

        if (instance == null) {

            instance = new ConfigManager();
        }

        return instance;
    }

    /**
     * get builder directly
     * @return builder for active mod
     */
    public static ConfigBuilder builder() {

        return get().getBuilder();
    }

}
