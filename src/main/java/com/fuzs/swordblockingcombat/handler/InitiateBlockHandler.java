package com.fuzs.swordblockingcombat.handler;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.EnumAction;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumActionResult;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class InitiateBlockHandler {

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem evt) {

        if (evt.getItemStack().getItem() instanceof ItemSword) {
            ItemStack stack = evt.getEntityPlayer().getHeldItemOffhand();
            if (stack.getItem().getUseAction(stack) == EnumAction.NONE) {
                evt.getEntityPlayer().setActiveHand(evt.getHand());
                evt.setCancellationResult(EnumActionResult.SUCCESS);
                evt.setCanceled(true);
            }
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onItemUseStart(LivingEntityUseItemEvent.Start evt) {

        if (evt.getItem().getItem() instanceof ItemSword) {
            evt.setDuration(72000);
        }

    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLivingHurt(LivingHurtEvent evt) {

        if (evt.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer) evt.getEntityLiving();
            if (!evt.getSource().isUnblockable() && player.getActiveItemStack().getItem() instanceof ItemSword && evt.getAmount() > 0.0F) {
                evt.setAmount((1.0F + evt.getAmount()) * 0.5F);
            }
        }

    }

}
