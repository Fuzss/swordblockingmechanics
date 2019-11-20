package com.fuzs.swordblockingcombat.handler;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.model.ItemCameraTransforms;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class RenderBlockingHandler {

    private final Minecraft mc = Minecraft.getInstance();

    @SuppressWarnings({"unused", "deprecation"})
    @SubscribeEvent
    public void renderSpecificHand(RenderSpecificHandEvent evt) {

        ItemStack stack = evt.getItemStack();
        if (stack.getItem() instanceof ItemSword) {
            EntityPlayerSP player = this.mc.player;
            if (player.isHandActive() && player.getActiveHand() == evt.getHand()) {
                GlStateManager.pushMatrix();
                boolean rightHanded = (evt.getHand() == EnumHand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == EnumHandSide.RIGHT;
                this.transformSideFirstPerson(rightHanded ? 1.0F : -1.0F, evt.getEquipProgress());
                this.mc.getFirstPersonRenderer().renderItemSide(player, stack, rightHanded ? ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND : ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded);
                GlStateManager.popMatrix();
                evt.setCanceled(true);
            }
        }

    }

    private void transformSideFirstPerson(float side, float equippedProg) {

        GlStateManager.translatef(side * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
        GlStateManager.translatef(side * -0.14142136F, 0.08F, 0.14142136F);
        GlStateManager.rotatef(-102.25F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(side * 13.365F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(side * 78.05F, 0.0F, 0.0F, 1.0F);

    }

}
