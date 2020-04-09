package com.fuzs.swordblockingcombat.client.handler;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import com.fuzs.swordblockingcombat.common.helper.BlockingItemHelper;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler;
import com.fuzs.swordblockingcombat.config.ConfigBuildHandler.AttackIndicator;
import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.entity.player.ClientPlayerEntity;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraft.client.settings.AttackIndicatorStatus;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.CrossbowItem;
import net.minecraft.item.FilledMapItem;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Effects;
import net.minecraft.util.*;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.GameType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.PlayerSPPushOutOfBlocksEvent;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.client.event.RenderLivingEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;

import java.util.Random;

@OnlyIn(Dist.CLIENT)
public class RenderBlockingHandler {

    private static final ResourceLocation GUI_ICONS_LOCATION = new ResourceLocation(SwordBlockingCombat.MODID, "textures/gui/icons.png");

    private final BlockingItemHelper blockingHelper = new BlockingItemHelper();
    private final Minecraft mc = Minecraft.getInstance();
    private final Random rand = new Random();
    private AttackIndicatorStatus attackIndicator = AttackIndicatorStatus.OFF;

    @SuppressWarnings("unused")
//    @SubscribeEvent
    public void onRenderLiving(final RenderLivingEvent.Pre<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>> evt) {

        if (evt.getEntity() instanceof AbstractClientPlayerEntity) {

            AbstractClientPlayerEntity player = (AbstractClientPlayerEntity) evt.getEntity();
            if (this.blockingHelper.isActiveItemStackBlocking(player)) {

                PlayerModel<AbstractClientPlayerEntity> model = evt.getRenderer().getEntityModel();
                boolean left1 = player.getActiveHand() == Hand.OFF_HAND && player.getPrimaryHand() == HandSide.RIGHT;
                boolean left2 = player.getActiveHand() == Hand.MAIN_HAND && player.getPrimaryHand() == HandSide.LEFT;
                if (left1 || left2) {

                    if (model.leftArmPose == BipedModel.ArmPose.ITEM) {

                        model.leftArmPose = BipedModel.ArmPose.BLOCK;
                    }
                } else {

                    if (model.rightArmPose == BipedModel.ArmPose.ITEM) {

                        model.rightArmPose = BipedModel.ArmPose.BLOCK;
                    }
                }
            }
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    @SubscribeEvent
    public void onRenderSpecificHand(final RenderSpecificHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        ItemStack stack = evt.getItemStack();
        if (player != null && player.getActiveHand() == evt.getHand() && this.blockingHelper.isActiveItemStackBlocking(player)) {

            GlStateManager.pushMatrix();
            boolean flag = evt.getHand() == Hand.MAIN_HAND;
            HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            boolean flag3 = handside == HandSide.RIGHT;
            this.transformSideFirstPerson(handside, evt.getEquipProgress());
            this.transformFirstPerson(handside, evt.getSwingProgress());
            this.transformBlockFirstPerson(handside);
            this.mc.getFirstPersonRenderer().renderItemSide(player, stack, flag3 ? net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                    net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !flag3);
            GlStateManager.popMatrix();
            evt.setCanceled(true);
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    @SubscribeEvent(priority = EventPriority.LOW)
    public void onRenderSpecificHand2(final RenderSpecificHandEvent evt) {

        ClientPlayerEntity player = this.mc.player;
        ItemStack stack = evt.getItemStack();

        if (stack.isEmpty() || stack.getItem() instanceof FilledMapItem || player == null) {

            return;
        }

        if (player.isHandActive() && player.getItemInUseCount() > 0 && player.getActiveHand() == evt.getHand()) {
            boolean rightHanded = (evt.getHand() == Hand.MAIN_HAND ? player.getPrimaryHand() : player.getPrimaryHand().opposite()) == HandSide.RIGHT;
            boolean flag = evt.getHand() == Hand.MAIN_HAND;
            HandSide handside = flag ? player.getPrimaryHand() : player.getPrimaryHand().opposite();
            float equippedProgress = evt.getEquipProgress();
            float partialTicks = evt.getPartialTicks();
            float swingProgress = evt.getSwingProgress();
            GlStateManager.pushMatrix();
            switch(stack.getUseAction()) {
                case NONE:
                    this.transformSideFirstPerson(handside, equippedProgress);
                    this.transformFirstPerson(handside, swingProgress);
                    break;
                case EAT:
                case DRINK:
                    this.transformEatFirstPerson(partialTicks, handside, stack);
                    this.transformSideFirstPerson(handside, equippedProgress);
                    this.transformFirstPerson(handside, swingProgress);
                    break;
                case BLOCK:
                    this.transformSideFirstPerson(handside, equippedProgress);
                    this.transformFirstPerson(handside, swingProgress);
                    break;
                case BOW:
                    this.transformSideFirstPerson(handside, equippedProgress);
                    this.transformFirstPerson(handside, swingProgress);
                    this.transformBowFirstPerson(partialTicks, handside, stack);
                    break;
                case SPEAR:
                    this.transformSideFirstPerson(handside, equippedProgress);
                    this.transformFirstPerson(handside, swingProgress);
                    this.transformSpearFirstPerson(partialTicks, handside, stack);
                    break;
                case CROSSBOW:
                    this.transformSideFirstPerson(handside, equippedProgress);
                    this.transformFirstPerson(handside, swingProgress);
                    this.transformCrossbowFirstPerson(partialTicks, handside, stack);
            }
            this.mc.getFirstPersonRenderer().renderItemSide(player, stack, rightHanded ? net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND :
                    net.minecraft.client.renderer.model.ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, !rightHanded);
            GlStateManager.popMatrix();
            evt.setCanceled(true);
        }
    }

    private void transformBowFirstPerson(float partialTicks, HandSide hand, ItemStack stack) {
        int i = hand == HandSide.RIGHT ? 1 : -1;
        GlStateManager.translatef((float)i * -0.2785682F, 0.18344387F, 0.15731531F);
        GlStateManager.rotatef(-13.935F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef((float)i * 35.3F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float)i * -9.785F, 0.0F, 0.0F, 1.0F);
        float f8 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0F);
        float f12 = f8 / 20.0F;
        f12 = (f12 * f12 + f12 * 2.0F) / 3.0F;
        if (f12 > 1.0F) {
            f12 = 1.0F;
        }

        if (f12 > 0.1F) {
            float f15 = MathHelper.sin((f8 - 0.1F) * 1.3F);
            float f18 = f12 - 0.1F;
            float f20 = f15 * f18;
            GlStateManager.translatef(f20 * 0.0F, f20 * 0.004F, f20 * 0.0F);
        }

        GlStateManager.translatef(f12 * 0.0F, f12 * 0.0F, f12 * 0.04F);
        GlStateManager.scalef(1.0F, 1.0F, 1.0F + f12 * 0.2F);
        GlStateManager.rotatef((float)i * 45.0F, 0.0F, -1.0F, 0.0F);
    }

    private void transformCrossbowFirstPerson(float partialTicks, HandSide hand, ItemStack stack) {
        int i = hand == HandSide.RIGHT ? 1 : -1;
        GlStateManager.translatef((float)i * -0.4785682F, -0.094387F, 0.05731531F);
        GlStateManager.rotatef(-11.935F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef((float)i * 65.3F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float)i * -9.785F, 0.0F, 0.0F, 1.0F);
        float f9 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0F);
        float f13 = f9 / (float) CrossbowItem.getChargeTime(stack);
        if (f13 > 1.0F) {
            f13 = 1.0F;
        }

        if (f13 > 0.1F) {
            float f16 = MathHelper.sin((f9 - 0.1F) * 1.3F);
            float f3 = f13 - 0.1F;
            float f4 = f16 * f3;
            GlStateManager.translatef(f4 * 0.0F, f4 * 0.004F, f4 * 0.0F);
        }

        GlStateManager.translatef(f13 * 0.0F, f13 * 0.0F, f13 * 0.04F);
        GlStateManager.scalef(1.0F, 1.0F, 1.0F + f13 * 0.2F);
        GlStateManager.rotatef((float)i * 45.0F, 0.0F, -1.0F, 0.0F);
    }

    private void transformSpearFirstPerson(float partialTicks, HandSide hand, ItemStack stack) {
        int i = hand == HandSide.RIGHT ? 1 : -1;
        GlStateManager.translatef(i * -0.5F, 0.7F, 0.1F);
        GlStateManager.rotatef(-55.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef(i * 35.3F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(i * -9.785F, 0.0F, 0.0F, 1.0F);
        float f7 = (float)stack.getUseDuration() - ((float)this.mc.player.getItemInUseCount() - partialTicks + 1.0F);
        float f11 = f7 / 10.0F;
        if (f11 > 1.0F) {
            f11 = 1.0F;
        }

        if (f11 > 0.1F) {
            float f14 = MathHelper.sin((f7 - 0.1F) * 1.3F);
            float f17 = f11 - 0.1F;
            float f19 = f14 * f17;
            GlStateManager.translatef(f19 * 0.0F, f19 * 0.004F, f19 * 0.0F);
        }

        GlStateManager.translatef(0.0F, 0.0F, f11 * 0.2F);
        GlStateManager.scalef(1.0F, 1.0F, 1.0F + f11 * 0.2F);
        GlStateManager.rotatef(i * 45.0F, 0.0F, -1.0F, 0.0F);
    }

    private void transformFirstPerson(HandSide hand, float swingProgress) {
        int i = hand == HandSide.RIGHT ? 1 : -1;
        float f = MathHelper.sin(swingProgress * swingProgress * (float)Math.PI);
        GlStateManager.rotatef((float)i * (45.0F + f * -20.0F), 0.0F, 1.0F, 0.0F);
        float f1 = MathHelper.sin(MathHelper.sqrt(swingProgress) * (float)Math.PI);
        GlStateManager.rotatef((float)i * f1 * -20.0F, 0.0F, 0.0F, 1.0F);
        GlStateManager.rotatef(f1 * -80.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef((float)i * -45.0F, 0.0F, 1.0F, 0.0F);
    }

    private void transformSideFirstPerson(HandSide hand, float equippedProg) {
        int i = hand == HandSide.RIGHT ? 1 : -1;
        GlStateManager.translatef((float)i * 0.56F, -0.52F + equippedProg * -0.6F, -0.72F);
    }

    private void transformEatFirstPerson(float partialTicks, HandSide hand, ItemStack stack) {
        float f = (float)this.mc.player.getItemInUseCount() - partialTicks + 1.0F;
        float f1 = f / (float)stack.getUseDuration();
        if (f1 < 0.8F) {
            float f2 = MathHelper.abs(MathHelper.cos(f / 4.0F * (float)Math.PI) * 0.1F);
            GlStateManager.translatef(0.0F, f2, 0.0F);
        }

        float f3 = 1.0F - (float)Math.pow((double)f1, 27.0D);
        int i = hand == HandSide.RIGHT ? 1 : -1;
        GlStateManager.translatef(f3 * 0.6F * (float)i, f3 * -0.5F, f3 * 0.0F);
        GlStateManager.rotatef((float) i * f3 * 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef(f3 * 10.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef((float)i * f3 * 30.0F, 0.0F, 0.0F, 1.0F);
    }

    /**
     * values taken from Minecraft snapshot 15w33b
     */
    private void transformBlockFirstPerson(HandSide hand) {

        int i = hand == HandSide.RIGHT ? 1 : -1;
        GlStateManager.translatef((float) i * -0.14142136F, 0.08F, 0.14142136F);
        GlStateManager.rotatef(-102.25F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotatef((float) i * 13.365F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotatef((float) i * 78.05F, 0.0F, 0.0F, 1.0F);
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay2(final RenderGameOverlayEvent.Pre evt) {

        if (evt.getType() != RenderGameOverlayEvent.ElementType.HEALTH || !(this.mc.getRenderViewEntity() instanceof PlayerEntity)) {

            return;
        }

        evt.setCanceled(true);
        PlayerEntity playerentity = (PlayerEntity) this.mc.getRenderViewEntity();
        boolean flag = playerentity.hurtResistantTime / 3 % 2 == 1;

        if (playerentity.hurtResistantTime <= 10) { // avoid blinking
            flag = false;
        }
        int i = MathHelper.ceil(playerentity.getHealth());
        int ticks = this.mc.ingameGUI.getTicks();
        this.rand.setSeed(ticks * 312871);
        IAttributeInstance iattributeinstance = playerentity.getAttribute(SharedMonsterAttributes.MAX_HEALTH);
        int i1 = evt.getWindow().getScaledWidth() / 2 - 91;
        int k1 = evt.getWindow().getScaledHeight() - 39;
        float f = (float) iattributeinstance.getValue();
        int l1 = MathHelper.ceil(playerentity.getAbsorptionAmount());
        int i2 = MathHelper.ceil((f + (float)l1) / 2.0F / 10.0F);
        int j2 = Math.max(10 - (i2 - 2), 3);
        int i3 = l1;
        int k3 = -1;
        if (playerentity.isPotionActive(Effects.REGENERATION)) {
            k3 = ticks % MathHelper.ceil(f + 5.0F);
        }

        this.mc.getProfiler().startSection("health");

        for(int l5 = MathHelper.ceil((f + (float)l1) / 2.0F) - 1; l5 >= 0; --l5) {
            int i6 = 16;
            if (playerentity.isPotionActive(Effects.POISON)) {
                i6 += 36;
            } else if (playerentity.isPotionActive(Effects.WITHER)) {
                i6 += 72;
            }

            int j4 = 0;
            if (flag) {
                j4 = 1;
            }

            int k4 = MathHelper.ceil((float)(l5 + 1) / 10.0F) - 1;
            int l4 = i1 + l5 % 10 * 8;
            int i5 = k1 - k4 * j2;
            if (i <= 4) {
                i5 += this.rand.nextInt(2);
            }

            if (i3 <= 0 && l5 == k3) {
                i5 -= 2;
            }

            int j5 = 0;
            if (playerentity.world.getWorldInfo().isHardcore()) {
                j5 = 5;
            }

            AbstractGui.blit(l4, i5, 16 + j4 * 9, 9 * j5, 9, 9, 256, 256);
            if (i3 > 0) {
                if (i3 == l1 && l1 % 2 == 1) {
                    AbstractGui.blit(l4, i5, i6 + 153, 9 * j5, 9, 9, 256, 256);
                    --i3;
                } else {
                    AbstractGui.blit(l4, i5, i6 + 144, 9 * j5, 9, 9, 256, 256);
                    i3 -= 2;
                }
            } else {
                if (l5 * 2 + 1 < i) {
                    AbstractGui.blit(l4, i5, i6 + 36, 9 * j5, 9, 9, 256, 256);
                }

                if (l5 * 2 + 1 == i) {
                    AbstractGui.blit(l4, i5, i6 + 45, 9 * j5, 9, 9, 256, 256);
                }
            }
        }

        this.mc.getProfiler().endSection();
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent evt) {

        // use shield indicator, but don't turn off if it's not enabled as it's a separate setting after all
        AttackIndicator attackIndicator = ConfigBuildHandler.SHIELD_INDICATOR.get();
        if (attackIndicator == AttackIndicator.OFF) {

            attackIndicator = AttackIndicator.CROSSHAIR;
        }

        boolean crosshair = evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS && attackIndicator == AttackIndicator.CROSSHAIR;
        boolean hotbar = evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR && attackIndicator == AttackIndicator.HOTBAR;
        if (!crosshair && !hotbar || this.mc.player == null || this.mc.playerController == null
                || this.mc.playerController.getCurrentGameType() == GameType.SPECTATOR && !this.mc.ingameGUI.func_212913_a(this.mc.objectMouseOver)) {

            return;
        }

        float f = (float) this.blockingHelper.getBlockUseDuration(this.mc.player) / (float) ConfigBuildHandler.PARRY_WINDOW.get();
        if (f >= 1.0F || !this.blockingHelper.isActiveItemStackBlocking(this.mc.player)) {

            return;
        }

        GameSettings gamesettings = this.mc.gameSettings;
        if (crosshair && gamesettings.thirdPersonView == 0) {

            if (!gamesettings.showDebugInfo || gamesettings.hideGUI || this.mc.player.hasReducedDebug() || gamesettings.reducedDebugInfo) {

                if (evt instanceof RenderGameOverlayEvent.Pre) {

                    this.attackIndicator = gamesettings.attackIndicator;
                    gamesettings.attackIndicator = AttackIndicatorStatus.OFF;
                } else if (evt instanceof RenderGameOverlayEvent.Post) {

                    gamesettings.attackIndicator = this.attackIndicator;
                    this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
                    GlStateManager.enableBlend();
                    GlStateManager.enableAlphaTest();
                    GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR,
                            GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

                    int width = this.mc.mainWindow.getScaledWidth() / 2 - 8;
                    int height = this.mc.mainWindow.getScaledHeight() / 2 - 7 + 16;
                    int i = (int) (f * 15.0F);
                    AbstractGui.blit(width, height, 36, 0, 16, 14, 256, 256);
                    AbstractGui.blit(width, height + i, 52, i, 16, 14 - i, 256, 256);
                }
            }
        }

        if (hotbar) {

            if (evt instanceof RenderGameOverlayEvent.Pre) {

                this.attackIndicator = gamesettings.attackIndicator;
                gamesettings.attackIndicator = AttackIndicatorStatus.OFF;
            } else if (evt instanceof RenderGameOverlayEvent.Post) {

                gamesettings.attackIndicator = this.attackIndicator;
                GlStateManager.enableRescaleNormal();
                GlStateManager.enableBlend();
                GlStateManager.blendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA,
                        GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                this.mc.getTextureManager().bindTexture(GUI_ICONS_LOCATION);
                GlStateManager.color4f(1.0F, 1.0F, 1.0F, 1.0F);

                int width = this.mc.mainWindow.getScaledWidth() / 2;
                int height = this.mc.mainWindow.getScaledHeight() - 20;
                width = this.mc.player.getPrimaryHand().opposite() == HandSide.RIGHT ? width - 91 - 22 : width + 91 + 6;
                int i = (int) (f * 19.0F);
                AbstractGui.blit(width, height, 0, 0, 18, 18, 256, 256);
                AbstractGui.blit(width, height + i, 18, i, 18, 18 - i, 256, 256);

                GlStateManager.disableRescaleNormal();
                GlStateManager.disableBlend();
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onPushOutOfBlocks(final PlayerSPPushOutOfBlocksEvent evt) {

        ClientPlayerEntity player = (ClientPlayerEntity) evt.getPlayer();
        double movementModifier = ConfigBuildHandler.WALKING_MODIFIER.get();
        if (movementModifier != 0.2F && !player.isPassenger() && this.blockingHelper.isActiveItemStackBlocking(player)) {

            player.movementInput.moveStrafe *= 5.0F * movementModifier;
            player.movementInput.moveForward *= 5.0F * movementModifier;
        }
    }

}
