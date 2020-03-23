package com.fuzs.swordblockingcombat.common.handler;

import com.fuzs.swordblockingcombat.common.helper.ItemBlockingHelper;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.item.EnumAction;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class InitiateBlockHandler {

    private final ItemBlockingHelper blockingHelper = new ItemBlockingHelper();

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(final PlayerInteractEvent.RightClickItem evt) {

        EntityPlayer player = evt.getEntityPlayer();
        if (ItemBlockingHelper.getCanStackBlock(evt.getItemStack())) {

            EnumAction action = player.getHeldItemOffhand().getItemUseAction();
            if (action == EnumAction.NONE || action == EnumAction.EAT && !player.canEat(false)) {

                player.setActiveHand(evt.getHand());
                // cause reequip animation, but don't swing hand
                evt.setCancellationResult(EnumActionResult.SUCCESS);
                evt.setCanceled(true);
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(final LivingEntityUseItemEvent.Start evt) {

        if (evt.getEntityLiving() instanceof EntityPlayer && ItemBlockingHelper.getCanStackBlock(evt.getItem())) {

            evt.setDuration(this.blockingHelper.swordUseDuration);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingAttack(final LivingAttackEvent evt) {

        if (ConfigBuildHandler.swordBlockingConfig.deflectProjectiles && evt.getEntityLiving() instanceof EntityPlayer && evt.getSource().getImmediateSource() instanceof EntityArrow
                && this.blockingHelper.getIsBlocking((EntityPlayer) evt.getEntityLiving())) {

            evt.setCanceled(true);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(final LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof EntityPlayer) {

            EntityPlayer player = (EntityPlayer) evt.getEntityLiving();
            float damage = evt.getAmount();

            if (damage > 0.0F && this.blockingHelper.getIsBlocking(player)) {

                this.blockingHelper.damageSword(player, damage);

                if (!evt.getSource().isUnblockable()) {

                    float reducedAmount = 1.0F + evt.getAmount() * (1.0F - (float) ConfigBuildHandler.swordBlockingConfig.blocked);
                    if (reducedAmount <= 1.0F) {
                        reducedAmount = 0.0F;
                    }

                    evt.setAmount(Math.min(evt.getAmount(), reducedAmount));
                }
            }
        }
    }

}
