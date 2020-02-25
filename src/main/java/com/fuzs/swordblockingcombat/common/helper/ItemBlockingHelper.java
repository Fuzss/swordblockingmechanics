package com.fuzs.swordblockingcombat.common.helper;

import com.fuzs.swordblockingcombat.util.ConfigBuildHandler;
import com.fuzs.swordblockingcombat.util.StringListParser;
import com.google.common.collect.Lists;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.MathHelper;

import java.util.Set;

public class ItemBlockingHelper {

    public final int swordUseDuration = 72000;

    private static Set<Item> exclude;
    private static Set<Item> include;

    public static void sync() {

        StringListParser parser = new StringListParser();
        exclude = parser.buildItemSetWithCondition(Lists.newArrayList(ConfigBuildHandler.swordBlockingConfig.exclude),
                item -> item instanceof ItemSword, "No instance of ItemSword");
        include = parser.buildItemSetWithCondition(Lists.newArrayList(ConfigBuildHandler.swordBlockingConfig.include),
                item -> !(item instanceof ItemSword), "Already is instance of ItemSword");
    }

    public static boolean getCanStackBlock(ItemStack stack) {

        Item item = stack.getItem();
        if (exclude != null && exclude.contains(item)) {
            return false;
        }

        if (item instanceof ItemSword) {
            return true;
        }

        return include != null && include.contains(item);
    }

    public void damageSword(EntityPlayer player, float damage) {

        if (ConfigBuildHandler.swordBlockingConfig.damageSword && damage >= 3.0F) {

            ItemStack stack = player.getActiveItemStack();
            ItemStack copy = stack.copy();
            int i = 1 + MathHelper.floor(damage);
            stack.damageItem(i, player);

            if (stack.isEmpty()) {
                EnumHand enumhand = player.getActiveHand();
                net.minecraftforge.event.ForgeEventFactory.onPlayerDestroyItem(player, copy, enumhand);

                if (enumhand == EnumHand.MAIN_HAND) {
                    player.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, ItemStack.EMPTY);
                } else {
                    player.setItemStackToSlot(EntityEquipmentSlot.OFFHAND, ItemStack.EMPTY);
                }

                player.resetActiveHand();
                player.playSound(SoundEvents.ITEM_SHIELD_BREAK, 0.8F, 0.8F + player.world.rand.nextFloat() * 0.4F);
            }
        }
    }

    public boolean getIsBlocking(EntityPlayer player) {

        boolean ready = this.swordUseDuration - player.getItemInUseCount() >= ConfigBuildHandler.swordBlockingConfig.blockDelay;
        return ready && getCanStackBlock(player.getActiveItemStack());
    }

}
