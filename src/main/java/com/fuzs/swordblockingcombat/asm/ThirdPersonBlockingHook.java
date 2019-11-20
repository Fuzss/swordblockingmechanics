package com.fuzs.swordblockingcombat.asm;

import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;

@SuppressWarnings("unused")
public class ThirdPersonBlockingHook {

    /**
     * set proper arm angles when blocking using a sword
     * accessed by the asm transformer bundled with this mod
     */
    public static void setArmRotationAngel(ModelRenderer rightArm, ModelRenderer leftArm) {

        Minecraft mc = Minecraft.getMinecraft();
        if (mc.player.getActiveItemStack().getItem() instanceof ItemSword) {
            if (mc.player.getActiveHand() == EnumHand.OFF_HAND) {
                leftArm.rotateAngleX = leftArm.rotateAngleX * 0.5F - 0.9424779F;
                leftArm.rotateAngleY = ((float)Math.PI / 6F);
            } else {
                rightArm.rotateAngleX = rightArm.rotateAngleX * 0.5F - 0.9424779F;
                rightArm.rotateAngleY = (-(float)Math.PI / 6F);
            }
        }

    }

}
