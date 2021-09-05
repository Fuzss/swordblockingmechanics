package com.fuzs.puzzleslib_sbm.element.side;

import net.minecraftforge.common.ForgeConfigSpec;

/**
 * implement this for elements with common capabilities
 */
public interface ICommonElement extends ISidedElement {

    /**
     * register common events and registry entry objects (blocks, items, etc.)
     */
    default void setupCommon() {

    }

    /**
     * setup for {@link net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent}
     * is always loaded no matter the element's state
     */
    default void loadCommon() {

    }

    /**
     * should basically clean up changes made by this element
     */
    default void unloadCommon() {

    }

    /**
     * build common config
     *
     * @param builder builder for common config
     */
    default void setupCommonConfig(ForgeConfigSpec.Builder builder) {

    }

    /**
     * @return description for this elements common config section
     */
    default String[] getCommonDescription() {

        return new String[0];
    }

}
