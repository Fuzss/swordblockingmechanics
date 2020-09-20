package com.fuzs.swordblockingcombat.config;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import net.minecraftforge.common.config.Config;

@SuppressWarnings("WeakerAccess")
@Config(modid = SwordBlockingCombat.MODID)
public class ConfigBuildHandler {

    @Config.Name("sword_blocking")
    @Config.Comment("Re-adds sword blocking in a very configurable way.")
    public static SwordBlockingConfig swordBlockingConfig = new SwordBlockingConfig();
    @Config.Name("classic_combat")
    @Config.Comment("Restores pre-combat update combat mechanics.")
    public static ClassicCombatConfig classicCombatConfig = new ClassicCombatConfig();
    @Config.Name("food_buffs")
    @Config.Comment({"Changes the way the player heals from food.", "Deactivates itself if \"Apple Core\" is installed."})
    public static FoodBuffsConfig foodBuffsConfig = new FoodBuffsConfig();

    public static class SwordBlockingConfig {

        @Config.Name("Blocked Damage Ratio")
        @Config.Comment("Percentage an incoming attack will be reduced by when blocking.")
        @Config.RangeDouble(min = 0.0, max = 1.0)
        public double blocked = 0.5;
        @Config.Name("Damage Sword")
        @Config.Comment("Damage sword when blocking an attack depending on the amount of damage blocked. Sword is only damaged when at least three damage points have been blocked, just like a shield.")
        public boolean damageSword = false;
        @Config.Name("Deflect Projectiles")
        @Config.Comment("Incoming projectiles such as arrows or tridents will ricochet while blocking.")
        public boolean deflectProjectiles = false;
        @Config.Name("Warm-Up Delay")
        @Config.Comment("Amount of ticks after which blocking using a sword is effective.")
        @Config.RangeInt(min = 0, max = 72000)
        public int blockDelay = 0;
        @Config.Name("Blocking Exclusion List")
        @Config.Comment("Swords to exclude from blocking. Intended for modded swords that already have their own right-click function. Format for every entry is \"<namespace>:<id>\".")
        public String[] exclude = new String[0];
        @Config.Name("Blocking Inclusion List")
        @Config.Comment("Items to include for blocking. Intended for modded swords that don't extend vanilla swords. Format for every entry is \"<namespace>:<id>\".")
        public String[] include = new String[0];

    }

    public static class ClassicCombatConfig {

        @Config.Name("Remove Attack Cooldown")
        @Config.Comment("Completely remove the attack cooldown as if it never even existed in the first place.")
        public boolean removeCooldown = true;
        @Config.Name("Require Sweeping Edge")
        @Config.Comment("Is the sweeping edge enchantment required to perform a sweep attack.")
        public boolean sweepingRequired = true;
        @Config.Name("Linear Knockback")
        @Config.Comment("Turns knockback resistance into a scale instead of being random.")
        public boolean linearKnockback = true;

    }

    public static class FoodBuffsConfig {

        @Config.Name("Food Ticker")
        @Config.Comment("\"CLASSIC\" option restores the pre-combat update system, \"COMBAT\" option introduces the changes from current combat snapshots, \"CUSTOM\" allows custom values to be supplied.")
        public EnumFoodTicker foodTicker = EnumFoodTicker.CLASSIC;
        @Config.Name("Regeneration Delay")
        @Config.Comment("Amount of ticks between regenerating when enough food is present. Only applies when \"Food Ticker\" is set to \"CUSTOM\".")
        @Config.RangeInt(min = 0)
        public int regenDelay = 80;
        @Config.Name("Regeneration Food Level")
        @Config.Comment("Food level required to be able to regenerate health. Only applies when \"Food Ticker\" is set to \"CUSTOM\".")
        @Config.RangeInt(min = 0, max = 20)
        public int regenThreshold = 18;
        @Config.Name("Regenerate From Food")
        @Config.Comment("Drain food instead of saturation when regenerating. Only applies when \"Food Ticker\" is set to \"CUSTOM\".")
        public boolean drainFood = false;

        @SuppressWarnings("unused")
        public enum EnumFoodTicker {
            DEFAULT(0), CLASSIC(1), COMBAT(2), CUSTOM(3);

            private final int id;

            EnumFoodTicker(int id) {
                this.id = id;
            }

            public int getId() {
                return this.id;
            }
        }

    }

}