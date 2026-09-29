package fuzs.swordblockingmechanics.common.client.handler;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.math.Axis;
import fuzs.puzzleslib.common.api.client.renderer.v2.RenderStateExtraData;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.swordblockingmechanics.common.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.common.config.ClientConfig;
import fuzs.swordblockingmechanics.common.handler.SwordBlockingHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.FirstPersonHandsAndItemsRenderer;
import net.minecraft.client.renderer.SubmitNodeCollector;
import net.minecraft.client.renderer.entity.state.EntityRenderState;
import net.minecraft.client.renderer.entity.state.HumanoidRenderState;
import net.minecraft.client.renderer.state.level.FirstPersonHandsAndItemsRenderState;
import net.minecraft.client.renderer.state.level.PlayerRenderState;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.util.context.ContextKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;

public class FirstPersonRenderingHandler {
    public static final ContextKey<Boolean> IS_BLOCKING_RENDER_PROPERTY_KEY = new ContextKey<>(SwordBlockingMechanics.id(
            "is_blocking"));

    public static void onExtractEntityRenderState(Entity entity, EntityRenderState entityRenderState, float partialTick) {
        if (entity instanceof LivingEntity player && entityRenderState instanceof HumanoidRenderState) {
            RenderStateExtraData.set(entityRenderState,
                    IS_BLOCKING_RENDER_PROPERTY_KEY,
                    SwordBlockingHandler.isActiveItemStackBlocking(player));
        }
    }

    public static EventResult onSubmitArmWithItem(FirstPersonHandsAndItemsRenderer handsAndItemsRenderer, PlayerRenderState playerState, FirstPersonHandsAndItemsRenderState state, float partialTicks, float xRot, InteractionHand hand, float attack, ItemStack itemStack, float inverseArmHeight, PoseStack poseStack, SubmitNodeCollector submitNodeCollector, int lightCoords) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player != null && player.getUsedItemHand() == hand && SwordBlockingHandler.isActiveItemStackBlocking(
                player)) {
            HumanoidArm humanoidArm = hand == InteractionHand.MAIN_HAND ? player.getMainArm() :
                    player.getMainArm().getOpposite();
            poseStack.pushPose();
            handsAndItemsRenderer.applyItemArmTransform(poseStack, humanoidArm, inverseArmHeight);
            if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).interactAnimations) {
                handsAndItemsRenderer.applyItemArmAttackTransform(poseStack, humanoidArm, attack);
            }

            applyBlockTransform(poseStack, humanoidArm);
            (hand == InteractionHand.MAIN_HAND ? state.mainHandRenderState : state.offHandRenderState).submit(poseStack,
                    submitNodeCollector,
                    lightCoords,
                    OverlayTexture.NO_OVERLAY,
                    0);
            poseStack.popPose();
            return EventResult.INTERRUPT;
        } else {
            return EventResult.PASS;
        }
    }

    /**
     * @see FirstPersonHandsAndItemsRenderer#submitArmWithItem
     */
    private static void applyBlockTransform(PoseStack poseStack, HumanoidArm humanoidArm) {
        int direction = humanoidArm == HumanoidArm.RIGHT ? 1 : -1;
        poseStack.translate(direction * -0.14142136F, 0.08F, 0.14142136F);
        poseStack.rotateDegrees(Axis.XP, -102.25F);
        poseStack.rotateDegrees(Axis.YP, direction * 13.365F);
        poseStack.rotateDegrees(Axis.ZP, direction * 78.05F);
    }
}
