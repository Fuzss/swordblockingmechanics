package com.fuzs.swordblockingcombat.config;

import com.google.common.collect.Maps;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.item.Item;
import net.minecraft.item.SwordItem;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Map;

public class ConfigSyncManager {

    private final StringListParser parser = new StringListParser();

    public void onModConfig(final ModConfig.ModConfigEvent evt) {

        if (evt.getConfig().getSpec() == ConfigBuildHandler.SPEC) {

            this.sync();
        }
    }

    private void sync() {

        // sword blocking
        ConfigValueHolder.SWORD_BLOCKING.exclude = this.parser.buildItemSetWithCondition(ConfigBuildHandler.EXCLUDE.get(),
                item -> item instanceof SwordItem, "No instance of SwordItem");
        ConfigValueHolder.SWORD_BLOCKING.include = this.parser.buildItemSetWithCondition(ConfigBuildHandler.INCLUDE.get(),
                item -> !(item instanceof SwordItem), "Already is instance of SwordItem");
        ConfigValueHolder.SWORD_BLOCKING.blockDelay = ConfigBuildHandler.BLOCK_DELAY.get();
        ConfigValueHolder.SWORD_BLOCKING.blocked = ConfigBuildHandler.BLOCKED.get().floatValue();
        ConfigValueHolder.SWORD_BLOCKING.damageSword = ConfigBuildHandler.DAMAGE_SWORD.get();
        ConfigValueHolder.SWORD_BLOCKING.deflectProjectiles = ConfigBuildHandler.DEFLECT_PROJECTILES.get();
        ConfigValueHolder.SWORD_BLOCKING.noSlow = ConfigBuildHandler.WALKING_MODIFIER.get().floatValue();
        // classic combat
        ConfigValueHolder.CLASSIC_COMBAT.removeCooldown = ConfigBuildHandler.REMOVE_ATTACK_COOLDOWN.get();
        ConfigValueHolder.CLASSIC_COMBAT.noTooltip = ConfigBuildHandler.NO_COOLDOWN_TOOLTIP.get();
        ConfigValueHolder.CLASSIC_COMBAT.hideIndicator = ConfigBuildHandler.DISABLE_ATTACK_INDICATOR.get();
        ConfigValueHolder.CLASSIC_COMBAT.boostSharpness = ConfigBuildHandler.BOOST_SHARPNESS.get();
        ConfigValueHolder.CLASSIC_COMBAT.sweepingRequired = ConfigBuildHandler.SWEEPING_REQUIRED.get();
        ConfigValueHolder.CLASSIC_COMBAT.noSweepingSmoke = ConfigBuildHandler.NO_SWEEPING_SMOKE.get();
        // material changer
        ConfigValueHolder.MATERIAL_CHANGER.attributes = this.syncAttributeMap();
        this.syncStackSize();
        this.syncDurability();
        // combat test
        ConfigValueHolder.COMBAT_TEST.noProjectileResistance = ConfigBuildHandler.NO_PROJECTILE_RESISTANCE.get();
        ConfigValueHolder.COMBAT_TEST.noAxePenalty = ConfigBuildHandler.NO_AXE_ATTACK_PENALTY.get();
        ConfigValueHolder.COMBAT_TEST.itemDelay = this.parser.buildItemMapWithCondition(ConfigBuildHandler.ITEM_DELAY.get(),
                (item, value) -> value >= 0.0 && value <= 72000.0, "Item delay out of bounds");
        ConfigValueHolder.COMBAT_TEST.shieldDelay = ConfigBuildHandler.SHIELD_DELAY.get() - 5;
        ConfigValueHolder.COMBAT_TEST.boostImpaling = ConfigBuildHandler.BOOST_IMPALING.get();
        ConfigValueHolder.COMBAT_TEST.dispenseTridents = ConfigBuildHandler.DISPENSE_TRIDENT.get();
        ConfigValueHolder.COMBAT_TEST.swingThroughGrass = ConfigBuildHandler.SWING_THROUGH_GRASS.get();
        ConfigValueHolder.COMBAT_TEST.coyoteTimer = ConfigBuildHandler.COYOTE_TIME.get();
        ConfigValueHolder.COMBAT_TEST.coyoteSmall = ConfigBuildHandler.COYOTE_SMALL.get();
        ConfigValueHolder.COMBAT_TEST.holdAttack = ConfigBuildHandler.HOLD_ATTACK.get();
        ConfigValueHolder.COMBAT_TEST.fistStrength = ConfigBuildHandler.FIST_STRENGTH.get();
        ConfigValueHolder.COMBAT_TEST.swingAnimation = ConfigBuildHandler.SWING_ANIMATION.get();
        ConfigValueHolder.COMBAT_TEST.itemProjectiles = ConfigBuildHandler.BETTER_PROJECTILES.get();
        ConfigValueHolder.COMBAT_TEST.fastSwitching = ConfigBuildHandler.FAST_SWITCHING.get();
        ConfigValueHolder.COMBAT_TEST.upwardsKnockback = ConfigBuildHandler.UPWARDS_KNOCKBACK.get();
        // food buffs
        ConfigValueHolder.FOOD_BUFFS.foodTicker = ConfigBuildHandler.FOOD_TICKER.get();
        ConfigValueHolder.FOOD_BUFFS.regenDelay = ConfigBuildHandler.REGEN_DELAY.get();
        ConfigValueHolder.FOOD_BUFFS.regenThreshold = ConfigBuildHandler.REGEN_THRESHOLD.get();
        ConfigValueHolder.FOOD_BUFFS.eatingSpeed = ConfigBuildHandler.EATING_SPEED.get();
        ConfigValueHolder.FOOD_BUFFS.regenThreshold = ConfigBuildHandler.REGEN_THRESHOLD.get();
        ConfigValueHolder.FOOD_BUFFS.sprintingLevel = ConfigBuildHandler.SPRINTING_LEVEL.get().floatValue();
        // better combat
        ConfigValueHolder.BETTER_COMBAT.attackingAllowsSprinting = ConfigBuildHandler.SPRINT_WHILE_ATTACKING.get();
        ConfigValueHolder.BETTER_COMBAT.retainEnergy = ConfigBuildHandler.RETAIN_ENERGY.get();
        ConfigValueHolder.BETTER_COMBAT.attackOnlyFull = ConfigBuildHandler.ATTACK_ONLY_FULL.get();
        ConfigValueHolder.BETTER_COMBAT.randomCrits = ConfigBuildHandler.RANDOM_CRITS.get();
        ConfigValueHolder.BETTER_COMBAT.critChance = ConfigBuildHandler.RANDOM_CRIT_CHANCE.get();
        ConfigValueHolder.BETTER_COMBAT.moreSweep = ConfigBuildHandler.MORE_SWEEP.get();
    }

    private Map<Item, Map<String, AttributeModifier>> syncAttributeMap() {

        Map<Item, Map<String, AttributeModifier>> map = Maps.newHashMap();
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_DAMAGE.get(), StringListParser.AttributeModifierType.ATTACK_DAMAGE, map);
        this.parser.buildAttributeMap(ConfigBuildHandler.ATTACK_SPEED.get(), StringListParser.AttributeModifierType.ATTACK_SPEED, map);
        return map;
    }

    private void syncStackSize() {

        this.parser.buildItemMapWithCondition(ConfigBuildHandler.MAX_STACK_SIZE.get(), (item, value) -> value >= 0.0 && value <= 64.0,
                "Stack size out of bounds").forEach((key, value) -> key.maxStackSize = value.intValue());
    }

    private void syncDurability() {

        this.parser.buildItemMapWithCondition(ConfigBuildHandler.TOOL_DURABILITY.get(), (item, value) -> item.isDamageable() && value >= 0.0,
                "Item can't be damaged or durability out of bounds").forEach((key, value) -> key.maxDamage = value.intValue());
    }

}
