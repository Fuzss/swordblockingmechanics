package com.fuzs.swordblockingcombat.client.handler;

import com.fuzs.swordblockingcombat.common.helper.ItemBlockingHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemTransformVec3f;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.lwjgl.util.vector.Vector3f;

public class SwordBlockingLayer extends LayerHeldItem {

    public SwordBlockingLayer(RenderLivingBase<?> livingEntityRendererIn) {

        super(livingEntityRendererIn);
    }

    @Override
    public void doRenderLayer(EntityLivingBase entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {

        boolean flag = entitylivingbaseIn.getPrimaryHand() == EnumHandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {

            GlStateManager.pushMatrix();
            if (this.livingEntityRenderer.getMainModel().isChild) {

                GlStateManager.translate(0.0F, 0.75F, 0.0F);
                GlStateManager.scale(0.5F, 0.5F, 0.5F);
            }

            this.renderHeldItem(entitylivingbaseIn, itemstack1, ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, EnumHandSide.RIGHT);
            this.renderHeldItem(entitylivingbaseIn, itemstack, ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, EnumHandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }

    @SuppressWarnings("deprecation")
    private void renderHeldItem(EntityLivingBase entityLivingBase, ItemStack stack, ItemCameraTransforms.TransformType transform, EnumHandSide handSide) {

        if (!stack.isEmpty()) {

            GlStateManager.pushMatrix();
            boolean leftHand = handSide == EnumHandSide.LEFT;
            if (entityLivingBase.isSneaking()) {

                GlStateManager.translate(0.0F, 0.2F, 0.0F);
            }

            // Forge: moved this call down, fixes incorrect offset while sneaking.
            this.translateToHand(handSide);
            if (entityLivingBase.isHandActive() && ItemBlockingHelper.getCanStackBlock(stack) && entityLivingBase.getActiveHand() == (leftHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND)) {

                GlStateManager.translate((float) (leftHand ? 1 : -1) / 16.0F, 0.4375F, 0.0625F);

                // blocking
                GlStateManager.translate(leftHand ? -0.035F : 0.05F, leftHand ? 0.045F : 0.0F, leftHand ? -0.135F : -0.1F);
                GlStateManager.rotate((leftHand ? -1 : 1) * -50.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(-10.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate((leftHand ? -1 : 1) * -60.0F, 0.0F, 0.0F, 1.0F);

                // old item layer
                GlStateManager.translate(0.0F, 0.1875F, 0.0F);
                GlStateManager.scale(0.625F, -0.625F, 0.625F);
                GlStateManager.rotate(-100.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(leftHand ? 35.0F : 45.0F, 0.0F, 1.0F, 0.0F);

                // old item renderer
                GlStateManager.translate(0.0F, -0.3F, 0.0F);
                GlStateManager.scale(1.5F, 1.5F, 1.5F);
                GlStateManager.rotate(50.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotate(335.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.rotate(0.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.translate(-0.9375F, -0.0625F, 0.0F);
                GlStateManager.translate(0.5F, 0.5F, 0.25F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(0.0F, 0.0F, 0.28125F);

                // revert 1.8+ model changes
                // Rotation={x=0.0, y=-90.0, z=55.0}, Translation={x=0.0, y=0.25, z=0.03125}, Scale={x=0.85, y=0.85, z=0.85} for diamond_sword
                // Rotation={x=0.0, y=90.0, z=-55.0}, Translation={x=0.0, y=0.25, z=0.03125}, Scale={x=0.85, y=0.85, z=0.85} for diamond_sword
                HeldItemHandler.applyTransformReverse(new ItemTransformVec3f(new Vector3f(0.0F, (leftHand ? 1 : -1) * 90.0F, (leftHand ? -1 : 1) * 55.0F), new Vector3f(0.0F, 0.25F, 0.03125F), new Vector3f(0.85F, 0.85F, 0.85F)), leftHand);
                // we'd normally want to use this, but for some reason the transform vector values always are the defaults; works in 1.14 though
//                IBakedModel ibakedmodel = Minecraft.getMinecraft().getRenderItem().getItemModelWithOverrides(stack, entityLivingBase.world, entityLivingBase);
//                ItemTransformVec3f vec3f = ibakedmodel.getItemCameraTransforms().getTransform(transform);
//                HeldItemHandler.applyTransformReverse(ibakedmodel.getItemCameraTransforms().getTransform(transform), leftHand);
            } else {

                GlStateManager.rotate(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translate((float) (leftHand ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            }

            Minecraft.getMinecraft().getItemRenderer().renderItemSide(entityLivingBase, stack, transform, leftHand);
            GlStateManager.popMatrix();
        }
    }

}
