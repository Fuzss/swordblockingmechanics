package com.fuzs.puzzleslib_sbm.config;

import com.fuzs.puzzleslib_sbm.PuzzlesLib;
import com.fuzs.puzzleslib_sbm.config.implementation.ConfigOption;
import com.fuzs.puzzleslib_sbm.config.implementation.OptionsBuilder;
import com.fuzs.puzzleslib_sbm.config.json.JsonConfigFileUtil;
import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import com.fuzs.puzzleslib_sbm.element.side.IClientElement;
import com.fuzs.puzzleslib_sbm.element.side.ICommonElement;
import com.fuzs.puzzleslib_sbm.element.side.IServerElement;
import com.fuzs.puzzleslib_sbm.util.INamespaceLocator;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.io.File;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * main config manager for all mods
 */
public class ConfigManager implements INamespaceLocator {

    /**
     * register configs from non-empty builders and add listener from active mod container to {@link #onModConfig}
     * @param modId mod id for config file name
     * @param allElements all elements for relevant <code>modId</code>
     * @param path optional directory inside of main config dir
     */
    public static void load(String modId, Collection<AbstractElement> allElements, String... path) {

        // create dummy element for general config section
        AbstractElement generalElement = AbstractElement.createEmpty(new ResourceLocation(modId, "general"));
        setupConfig(allElements, generalElement, ModConfig.Type.COMMON, element -> element instanceof ICommonElement, false, modId, path);
        setupConfig(allElements, generalElement, ModConfig.Type.CLIENT, element -> element instanceof IClientElement, true, modId, path);
        setupConfig(allElements, generalElement, ModConfig.Type.SERVER, element -> element instanceof IServerElement, true, modId, path);

        FMLJavaModLoadingContext.get().getModEventBus().addListener((ModConfig.ModConfigEvent evt) -> onModConfig(evt, new HashSet<AbstractElement>(allElements) {{

            this.add(generalElement);
        }}));
    }

    /**
     * @param allElements all elements for relevant <code>modId</code>
     * @param generalElement dummy general category element for this mod, also used for reloading config values
     * @param type type of config to create
     * @param isCorrectSide predicate for testing element side
     * @param ignoreCommon are common elements permitted
     * @param modId mod id for config file name
     * @param path optional directory inside of main config dir
     */
    private static void setupConfig(Collection<AbstractElement> allElements, AbstractElement generalElement, ModConfig.Type type, Predicate<AbstractElement> isCorrectSide, boolean ignoreCommon, String modId, String... path) {

        OptionsBuilder optionsBuilder = new OptionsBuilder(type);
        Set<AbstractElement> elementsAtSide = allElements.stream().filter(isCorrectSide).collect(Collectors.toSet());
        create(optionsBuilder, generalElement, builder -> elementsAtSide.stream()
                .filter(element -> !ignoreCommon || !(element instanceof ICommonElement))
                .forEach(element -> element.setupGeneralConfig(builder)));

        for (AbstractElement element : elementsAtSide) {

            create(optionsBuilder, element, ((ICommonElement) element)::setupCommonConfig, ((ICommonElement) element).getCommonDescription());
        }

        ModLoadingContext.get().registerConfig(type, optionsBuilder.build(), getFileName(modId, type, path));
    }

    /**
     * wrap creation of a new category
     * @param builder       builder for config type
     * @param element       element for this new category
     * @param setupConfig   builder for category
     * @param comment       comments to add to category
     */
    private static void create(OptionsBuilder builder, AbstractElement element, Consumer<OptionsBuilder> setupConfig, String... comment) {

        if (comment.length != 0) {

            builder.comment(comment);
        }

        builder.push(element);
        setupConfig.accept(builder);
        builder.pop(element);
    }

    /**
     * fires on both loading and reloading, loading phase is required for initial setup
     * @param evt event provided by Forge
     */
    private static void onModConfig(final ModConfig.ModConfigEvent evt, Set<AbstractElement> elements) {

        syncOptions(elements, evt.getConfig().getType(), evt instanceof ModConfig.Reloading);
    }

    /**
     * sync all config entries and notify all listeners
     * @param elements all elements for relevant mod
     * @param type config type for this listener
     */
    public static void syncOptions(Set<AbstractElement> elements, ModConfig.Type type) {

        syncOptions(elements, type, true);
    }

    /**
     * sync config entries for specific type of config
     * call listeners for type as the config has somehow been loaded
     * @param elements all elements for relevant mod
     * @param type config type for this listener
     */
    private static void syncOptions(Set<AbstractElement> elements, ModConfig.Type type, boolean log) {

        Collection<ConfigOption<?>> options = getAllOptions(elements, type, true);
        if (!options.isEmpty()) {

            options.forEach(ConfigOption::sync);
            if (log) {

                PuzzlesLib.LOGGER.info("Reloaded " + type.extension() + " config options for " + elements.stream()
                        .map(AbstractElement::getRegistryName)
                        .map(ResourceLocation::toString)
                        .collect(Collectors.joining(", ")));
            }
        }
    }

    /**
     * @param elements all elements for relevant mod
     * @param type config type for this listener
     * @param onlyEnabled only get options from enabled elements
     * @return collection of enabled entries only for this mod and type
     */
    public static Collection<ConfigOption<?>> getAllOptions(Set<AbstractElement> elements, ModConfig.Type type, boolean onlyEnabled) {

        return elements.stream()
                .filter(entry -> !onlyEnabled || entry.isEnabled())
                .flatMap(element -> element.getOptions().stream())
                .filter(option -> option.isType(type))
                .collect(Collectors.toSet());
    }

    /**
     * @param type type of config
     * @param modId mod id this config belongs to
     * @return config name as if it were generated by Forge itself
     */
    public static String getFileName(String modId, ModConfig.Type type) {

        return String.format("%s-%s.toml", modId, type.extension());
    }

    /**
     * put config into it's own folder when there are multiples
     * @param type type of config
     * @param modId mod id this config belongs to
     * @return name lead by folder
     */
    public static String getFileName(String modId, ModConfig.Type type, String... path) {

        String prefix = String.join(File.separator, path);
        JsonConfigFileUtil.mkdirs(prefix);

        return prefix + File.separator + getFileName(modId, type);
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

}
