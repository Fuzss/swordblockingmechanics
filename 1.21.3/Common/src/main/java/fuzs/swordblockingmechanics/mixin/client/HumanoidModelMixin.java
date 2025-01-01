package fuzs.swordblockingmechanics.mixin.client;

import fuzs.swordblockingmechanics.SwordBlockingMechanics;
import fuzs.swordblockingmechanics.config.ClientConfig;
import fuzs.swordblockingmechanics.handler.SwordBlockingHandler;
import net.minecraft.client.model.AgeableListModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.model.geom.ModelPart;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(HumanoidModel.class)
abstract class HumanoidModelMixin<T extends LivingEntity> extends AgeableListModel<T> {
    @Shadow
    public ModelPart rightArm;
    @Shadow
    public ModelPart leftArm;

    @Inject(method = "setupAnim", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/model/HumanoidModel;setupAttackAnimation(Lnet/minecraft/world/entity/LivingEntity;F)V"))
    public void setupAnim(T entity, float limbSwing, float limbSwingAmount, float ageInTicks, float netHeadYaw, float headPitch, CallbackInfo callback) {
        if (entity instanceof Player player) {
            if (SwordBlockingHandler.isActiveItemStackBlocking(player)) {
                if (entity.getUsedItemHand() == InteractionHand.OFF_HAND) {
                    this.leftArm.xRot = this.leftArm.xRot - ((float) Math.PI * 2.0F) / 10.0F;
                    if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).simpleBlockingPose) {
                        this.leftArm.yRot = ((float) Math.PI / 6.0F);
                    }
                } else {
                    this.rightArm.xRot = this.rightArm.xRot - ((float) Math.PI * 2.0F) / 10.0F;
                    if (SwordBlockingMechanics.CONFIG.get(ClientConfig.class).simpleBlockingPose) {
                        this.rightArm.yRot = ((float) -Math.PI / 6.0F);
                    }
                }
            }
        }
    }
}
