package com.fuzs.swordblockingcombat.common.handler;

import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.fuzs.swordblockingcombat.util.ReflectionHelper;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.CriticalHitEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ClassicCombatHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onAttackEntity(final AttackEntityEvent evt) {

        // disable cooldown right before every attack
        if (ConfigBuildHandler.classicCombatConfig.removeCooldown) {

            ReflectionHelper.setTicksSinceLastSwing(evt.getEntityPlayer(), (int) Math.ceil(evt.getEntityPlayer().getCooldownPeriod()));
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onCriticalHit(final CriticalHitEvent evt) {

        // prevent sweeping from taking effect unless the enchantment is in place, onGround flag is reset the next tick anyways
        if (ConfigBuildHandler.classicCombatConfig.sweepingRequired && EnchantmentHelper.getSweepingDamageRatio(evt.getEntityPlayer()) == 0.0F) {

            evt.getEntityPlayer().onGround = false;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingKnockBack(final LivingKnockBackEvent evt) {

        if (ConfigBuildHandler.classicCombatConfig.linearKnockback) {

            EntityLivingBase entity = evt.getEntityLiving();
            float strength = evt.getStrength();

            // turns knockback resistance into scale instead of being random
            strength = (float) (strength * (1.0 - entity.getEntityAttribute(SharedMonsterAttributes.KNOCKBACK_RESISTANCE).getAttributeValue()));
            if (strength > 0.0F) {

                entity.isAirBorne = true;
                Vec3d vec3d = new Vec3d(entity.motionX, entity.motionY, entity.motionZ);
                Vec3d vec3d1 = new Vec3d(evt.getRatioX(), 0.0, evt.getRatioZ()).normalize().scale(strength);
                entity.motionX = vec3d.x / 2.0 - vec3d1.x;
                entity.motionY = entity.onGround ? Math.min(0.4D, vec3d.y / 2.0D + (double)strength) : vec3d.y;
                entity.motionZ = vec3d.z / 2.0 - vec3d1.z;
            }

            evt.setCanceled(true);
        }
    }

}
