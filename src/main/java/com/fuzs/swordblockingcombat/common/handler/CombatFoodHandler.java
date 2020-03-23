package com.fuzs.swordblockingcombat.common.handler;

import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.fuzs.swordblockingcombat.util.ReflectionHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;
import net.minecraft.util.FoodStats;
import net.minecraft.world.EnumDifficulty;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CombatFoodHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onEntityJoinWorld(final EntityJoinWorldEvent evt) {

        // replace food stats with one having some tweaks in the tick method
        if (ConfigBuildHandler.foodBuffsConfig.foodTicker.getId() != 0 && evt.getEntity() instanceof EntityPlayer) {
            ReflectionHelper.setFoodStats((EntityPlayer) evt.getEntity(), new CombatFoodStats(ReflectionHelper.getFoodStats(((EntityPlayer) evt.getEntity()))));
        }
    }

    private static class CombatFoodStats extends FoodStats {

        private CombatFoodStats(FoodStats oldStats) {

            ReflectionHelper.setFoodLevel(this, ReflectionHelper.getFoodLevel(oldStats));
            ReflectionHelper.setFoodSaturationLevel(this, ReflectionHelper.getFoodSaturationLevel(oldStats));
            ReflectionHelper.setFoodExhaustionLevel(this, ReflectionHelper.getFoodExhaustionLevel(oldStats));
            ReflectionHelper.setFoodTimer(this, ReflectionHelper.getFoodTimer(oldStats));
        }

        @Override
        public void onUpdate(EntityPlayer player) {
            
            int foodLevel = ReflectionHelper.getFoodLevel(this);
            float foodSaturationLevel = ReflectionHelper.getFoodSaturationLevel(this);
            float foodExhaustionLevel = ReflectionHelper.getFoodExhaustionLevel(this);
            int foodTimer = ReflectionHelper.getFoodTimer(this);

            EnumDifficulty difficulty = player.world.getDifficulty();
            if (foodExhaustionLevel > 4.0F) {
                foodExhaustionLevel -= 4.0F;
                if (foodSaturationLevel > 0.0F) {
                    foodSaturationLevel = Math.max(foodSaturationLevel - 1.0F, 0.0F);
                } else if (difficulty != EnumDifficulty.PEACEFUL) {
                    foodLevel = Math.max(foodLevel - 1, 0);
                }
            }

            int id = ConfigBuildHandler.foodBuffsConfig.foodTicker.getId();
            int delay = id == 2 ? 60 : id == 3 ? ConfigBuildHandler.foodBuffsConfig.regenDelay : 80;
            int threshold = id == 2 ? 6 : id == 3 ? ConfigBuildHandler.foodBuffsConfig.regenThreshold : 18;
            boolean naturalRegen = player.world.getGameRules().getBoolean("naturalRegeneration");
            if (naturalRegen && foodLevel >= threshold && player.shouldHeal()) {
                ++foodTimer;
                if (foodTimer >= delay) {
                    player.heal(1.0F);
                    if (id == 2 || id == 3 && ConfigBuildHandler.foodBuffsConfig.drainFood) {
                        foodLevel = Math.max(foodLevel - 1, 0);
                    } else {
                        this.addExhaustion(3.0F); // is 6.0F in current vanilla
                    }
                    foodTimer = 0;
                }
            } else if (foodLevel <= 0) {
                ++foodTimer;
                if (foodTimer >= delay) {
                    if (player.getHealth() > 10.0F || difficulty == EnumDifficulty.HARD || player.getHealth() > 1.0F && difficulty == EnumDifficulty.NORMAL) {
                        player.attackEntityFrom(DamageSource.STARVE, 1.0F);
                    }

                    foodTimer = 0;
                }
            } else {
                foodTimer = 0;
            }

            ReflectionHelper.setFoodLevel(this, foodLevel);
            ReflectionHelper.setFoodSaturationLevel(this, foodSaturationLevel);
            ReflectionHelper.setFoodExhaustionLevel(this, foodExhaustionLevel);
            ReflectionHelper.setFoodTimer(this, foodTimer);
        }

    }

}
