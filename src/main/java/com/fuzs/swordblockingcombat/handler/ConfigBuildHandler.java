package com.fuzs.swordblockingcombat.handler;

import com.google.common.collect.Lists;
import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

@SuppressWarnings("WeakerAccess")
public class ConfigBuildHandler {

    private static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();

    public static final GeneralConfig GENERAL_CONFIG = new GeneralConfig("general");

    public static class GeneralConfig {

        public final ForgeConfigSpec.ConfigValue<List<String>> exclude;
        public final ForgeConfigSpec.ConfigValue<List<String>> include;
        public final ForgeConfigSpec.DoubleValue blocked;

        private GeneralConfig(String name) {

            BUILDER.push(name);

            this.exclude = ConfigBuildHandler.BUILDER.comment("Swords to exclude from blocking. Intended for modded swords that already have their own right-click function. Format for every entry is \"<namespace>:<id>\".").define("Blocking Exclusion List", Lists.newArrayList());
            this.include = ConfigBuildHandler.BUILDER.comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords. Format for every entry is \"<namespace>:<id>\".").define("Blocking Inclusion List", Lists.newArrayList());
            this.blocked = ConfigBuildHandler.BUILDER.comment("Percentage an incoming attack will be reduced by when blocking.").defineInRange("Blocked Damage Ratio", 0.5, 0.0, 1.0);

            BUILDER.pop();

        }

    }

    public static final ForgeConfigSpec SPEC = BUILDER.build();

}