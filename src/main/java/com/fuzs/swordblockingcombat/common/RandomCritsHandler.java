package com.fuzs.swordblockingcombat.common;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RandomCritsHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onCriticalHit(CriticalHitEvent evt) {

        if (!ConfigValueHolder.BETTER_COMBAT.randomCrits) {

            return;
        }

        if (evt.isVanillaCritical()) {

            evt.setDamageModifier(1.0F);
        } else if (evt.getPlayer().getCooledAttackStrength(0.5F) == 1.0F && evt.getPlayer().getRNG().nextDouble() < ConfigValueHolder.BETTER_COMBAT.critChance) {

            evt.setDamageModifier(1.5F);
        }
    }

    public static void restoreSprinting(PlayerEntity player, int knockback) {

        // knockback modifier will have changed +1 if the player was sprinting originally, therefor we now reset the sprinting flag
        // it's possible that if there's still an attack cooldown left +1 wasn't added and sprinting won't be restored,
        // but this is what classic combat is here to disable in the first place
        if (EnchantmentHelper.getKnockbackModifier(player) < knockback) {

            player.setSprinting(true);
        }
    }

}
