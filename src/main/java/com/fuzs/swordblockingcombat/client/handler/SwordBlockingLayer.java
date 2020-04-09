package com.fuzs.swordblockingcombat.client.handler;

import com.fuzs.swordblockingcombat.common.helper.BlockingItemHelper;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.entity.IEntityRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.renderer.model.IBakedModel;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Hand;
import net.minecraft.util.HandSide;

import javax.annotation.Nonnull;

@SuppressWarnings("deprecation")
public class SwordBlockingLayer extends HeldItemLayer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> {

    private final BlockingItemHelper blockingHelper = new BlockingItemHelper();

    public SwordBlockingLayer(IEntityRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> p_i50934_1_) {
        super(p_i50934_1_);
    }

    @Override
    public void render(@Nonnull AbstractClientPlayerEntity entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scalef) {

        boolean flag = entitylivingbaseIn.getPrimaryHand() == HandSide.RIGHT;
        ItemStack itemstack = flag ? entitylivingbaseIn.getHeldItemOffhand() : entitylivingbaseIn.getHeldItemMainhand();
        ItemStack itemstack1 = flag ? entitylivingbaseIn.getHeldItemMainhand() : entitylivingbaseIn.getHeldItemOffhand();
        if (!itemstack.isEmpty() || !itemstack1.isEmpty()) {

            GlStateManager.pushMatrix();
            if (this.getEntityModel().isChild) {

                GlStateManager.translatef(0.0F, 0.75F, 0.0F);
                GlStateManager.scalef(0.5F, 0.5F, 0.5F);
            }

            this.renderHeldItem(entitylivingbaseIn, itemstack1, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, HandSide.RIGHT);
            this.renderHeldItem(entitylivingbaseIn, itemstack, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, HandSide.LEFT);
            GlStateManager.popMatrix();
        }
    }

    private void renderHeldItem(AbstractClientPlayerEntity entityLivingBase, ItemStack stack, net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType transform, HandSide handSide) {

        if (!stack.isEmpty()) {

            GlStateManager.pushMatrix();
            boolean leftHand = handSide == HandSide.LEFT;
            if (entityLivingBase.isSneaking()) {
                GlStateManager.translatef(0.0F, 0.2F, 0.0F);
            }

            // Forge: moved this call down, fixes incorrect offset while sneaking.
            this.translateToHand(handSide);
            if (this.blockingHelper.isActiveItemStackBlocking(entityLivingBase) && entityLivingBase.getActiveHand() == (leftHand ? Hand.OFF_HAND : Hand.MAIN_HAND)) {

                GlStateManager.translatef((float) (leftHand ? 1 : -1) / 16.0F, 0.4375F, 0.0625F);

                // blocking
                GlStateManager.translatef(leftHand ? -0.035F : 0.05F, leftHand ? 0.045F : 0.0F, leftHand ? -0.135F : -0.1F);
                GlStateManager.rotatef((float) (leftHand ? -1 : 1) * -50.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(-10.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef((float) (leftHand ? -1 : 1) * -60.0F, 0.0F, 0.0F, 1.0F);

                // old item layer
                GlStateManager.translatef(0.0F, 0.1875F, 0.0F); // moved from this.field_177206_a.func_82422_c();
                float var14 = 0.625F;
                GlStateManager.scalef(var14, -var14, var14);
                GlStateManager.rotatef(-100.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(leftHand ? 35.0F : 45.0F, 0.0F, 1.0F, 0.0F);

                // old item renderer
                GlStateManager.translatef(0.0F, -0.3F, 0.0F);
                GlStateManager.scalef(1.5F, 1.5F, 1.5F);
                GlStateManager.rotatef(50.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.rotatef(335.0F, 0.0F, 0.0F, 1.0F);
                GlStateManager.translatef(-0.9375F, -0.0625F, 0.0F);
                GlStateManager.translatef(0.5F, 0.5F, 0.25F);
                GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translatef(0.0F, 0.0F, 0.28125F);

                // revert 1.8+ model changes
                // Rotation=(0.0, 90.0, -35.0), Translation=(0.0, 0.078125, -0.21875), Scale=(0.85, 0.85, 0.85)
                // Rotation={x=0.0, y=-90.0, z=55.0}, Translation={x=0.0, y=0.25, z=0.03125}, Scale={x=0.85, y=0.85, z=0.85} for diamond_sword
//                            applyTransformReverse(new ItemTransformVec3f(new Vector3f(0.0F, (float) (leftHand ? 1 : -1) * 90.0F, (float) (leftHand ? -1 : 1) * 55.0F), new Vector3f(0.0F, 0.25F, 0.03125F), new Vector3f(0.85F, 0.85F, 0.85F)), leftHand);
                IBakedModel ibakedmodel = Minecraft.getInstance().getItemRenderer().getItemModelWithOverrides(stack, entityLivingBase.world, entityLivingBase);
                HeldItemHandler.applyTransformReverse(ibakedmodel.getItemCameraTransforms().getTransform(transform), leftHand);
//                            System.out.println("Rotation=" + vec3fToString(vec3f.rotation) + ", Translation=" + vec3fToString(vec3f.translation) + ", Scale=" + vec3fToString(vec3f.scale) + " for " + stack.getItem());
            } else {

                GlStateManager.rotatef(-90.0F, 1.0F, 0.0F, 0.0F);
                GlStateManager.rotatef(180.0F, 0.0F, 1.0F, 0.0F);
                GlStateManager.translatef((float) (leftHand ? -1 : 1) / 16.0F, 0.125F, -0.625F);
            }

            Minecraft.getInstance().getFirstPersonRenderer().renderItemSide(entityLivingBase, stack, transform, leftHand);
            GlStateManager.popMatrix();
        }
    }

}
