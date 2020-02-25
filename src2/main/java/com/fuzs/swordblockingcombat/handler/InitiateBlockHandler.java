package com.fuzs.swordblockingcombat.handler;

import com.fuzs.swordblockingcombat.helper.EligibleItemHelper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.EquipmentSlotType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.UseAction;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.Hand;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InitiateBlockHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem evt) {

        if (EligibleItemHelper.check(evt.getItemStack())) {

            PlayerEntity player = evt.getPlayer();
            ItemStack stack = player.getHeldItemOffhand();
            Item item = stack.getItem();
            if (item.getUseAction(stack) == UseAction.NONE || item.getFood() != null && !player.canEat(item.getFood().canEatWhenFull())) {

                player.setActiveHand(evt.getHand());
                evt.setCancellationResult(ActionResultType.SUCCESS);
                evt.setCanceled(true);
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start evt) {

        if (EligibleItemHelper.check(evt.getItem())) {
            evt.setDuration(72000);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof PlayerEntity) {
            PlayerEntity player = (PlayerEntity) evt.getEntityLiving();
            if (!evt.getSource().isUnblockable() && EligibleItemHelper.check(player.getActiveItemStack()) && evt.getAmount() > 0.0F) {

                float reducedAmount = 1.0F + evt.getAmount() * (1.0F - ConfigBuildHandler.GENERAL_CONFIG.blocked.get().floatValue());
                if (reducedAmount <= 1.0F) {
                    reducedAmount = 0.0F;
                }
                evt.setAmount(Math.min(evt.getAmount(), reducedAmount));
            }
        }

    }

}
