package fuzs.swordblockingmechanics.common.mixin.client;

import fuzs.swordblockingmechanics.common.init.ModRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.FirstPersonHandsAndItems;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FirstPersonHandsAndItems.class)
abstract class FirstPersonHandsAndItemsMixin {

    @Inject(method = "itemUsed", at = @At("HEAD"), cancellable = true)
    public void itemUsed(InteractionHand hand, CallbackInfo callback) {
        // Don't play the re-equip animation when beginning to use an item, like shield or bow.
        LocalPlayer player = Minecraft.getInstance().player;
        if (player.isUsingItem() && player.getUsedItemHand() == hand) {
            if (player.getUseItem().is(ModRegistry.CAN_PERFORM_SWORD_BLOCKING_ITEM_TAG)) {
                callback.cancel();
            }
        }
    }
}
