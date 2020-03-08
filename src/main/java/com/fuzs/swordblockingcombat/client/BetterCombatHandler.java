package com.fuzs.swordblockingcombat.client;

import com.fuzs.swordblockingcombat.config.ConfigValueHolder;
import com.fuzs.swordblockingcombat.network.NetworkHandler;
import com.fuzs.swordblockingcombat.network.message.SimpleSwingMessage;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.FirstPersonRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPlayerDiggingPacket;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.client.event.FOVUpdateEvent;
import net.minecraftforge.client.event.InputEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.player.AttackEntityEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class BetterCombatHandler {

    private final Minecraft mc = Minecraft.getInstance();

    private int ticksSinceLastSwing;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClickInput(InputEvent.ClickInputEvent evt) {

        if (this.mc.player != null && this.mc.objectMouseOver != null && evt.isAttack()) {

            if (this.mc.objectMouseOver.getType() != RayTraceResult.Type.BLOCK) {

                // cancel attack when attack cooldown is not completely recharged
                if (ConfigValueHolder.BETTER_COMBAT.attackOnlyFull && this.mc.player.getCooledAttackStrength(0.5F) < 1.0F) {
                    evt.setSwingHand(false);
                    evt.setCanceled(true);
                    return;
                }

                // save ticksSinceLastSwing for resetting later
                // also prevent hand from swinging as this would cause the cooldown to be reset on the server side
                if (ConfigValueHolder.BETTER_COMBAT.retainEnergy && this.mc.objectMouseOver.getType() == RayTraceResult.Type.MISS) {

                    this.ticksSinceLastSwing = this.mc.player.ticksSinceLastSwing;
                    if (evt.shouldSwingHand()) {

                        evt.setSwingHand(false);
                        this.mc.player.func_226292_a_(evt.getHand(), false);
                        NetworkHandler.getInstance().sendToServer(new SimpleSwingMessage(evt.getHand()));
                    }
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onLeftClickEmpty(PlayerInteractEvent.LeftClickEmpty evt) {

        // reset ticksSinceLastSwing to previously saved value
        if (ConfigValueHolder.BETTER_COMBAT.retainEnergy && this.mc.player != null) {

            this.mc.player.ticksSinceLastSwing = Math.max(this.mc.player.ticksSinceLastSwing, this.ticksSinceLastSwing);
        }
    }

    // =========== TESTING START ==============//

    private boolean prematureAttempt;
    private boolean anotherAttempt;
    private int i = 0;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void on(InputEvent.ClickInputEvent evt) {

        if (evt.isUseItem() && evt.getHand() == Hand.OFF_HAND) {
            prematureAttempt = true;
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void on(FOVUpdateEvent evt) {

        FirstPersonRenderer firstPersonRenderer = this.mc.getFirstPersonRenderer();
        if (this.prematureAttempt && firstPersonRenderer.equippedProgressOffHand != 0) {
            this.prematureAttempt = false;
            ItemStack itemstack = this.mc.player.getHeldItem(Hand.OFF_HAND);
            this.mc.player.setHeldItem(Hand.OFF_HAND, this.mc.player.getHeldItem(Hand.MAIN_HAND));
            this.mc.player.setHeldItem(Hand.MAIN_HAND, itemstack);
            this.mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_HELD_ITEMS, BlockPos.ZERO, Direction.DOWN));
            this.anotherAttempt = true;
            this.mc.clickMouse();
            this.anotherAttempt = false;
            ItemStack itemstack2 = this.mc.player.getHeldItem(Hand.OFF_HAND);
            this.mc.player.setHeldItem(Hand.OFF_HAND, this.mc.player.getHeldItem(Hand.MAIN_HAND));
            this.mc.player.setHeldItem(Hand.MAIN_HAND, itemstack2);
            this.mc.getConnection().sendPacket(new CPlayerDiggingPacket(CPlayerDiggingPacket.Action.SWAP_HELD_ITEMS, BlockPos.ZERO, Direction.DOWN));
            this.mc.player.swingArm(Hand.OFF_HAND);
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void on(LivingDamageEvent evt) {
        System.out.println(evt.getAmount());
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void on(AttackEntityEvent evt) {
        System.out.println(evt.getPlayer().getHeldItemMainhand());
    }


}
