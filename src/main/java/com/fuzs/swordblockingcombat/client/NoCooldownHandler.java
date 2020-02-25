package com.fuzs.swordblockingcombat.client;

import com.fuzs.swordblockingcombat.util.ConfigBuildHandler;
import com.fuzs.swordblockingcombat.util.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.entity.Entity;
import net.minecraftforge.client.event.RenderGameOverlayEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class NoCooldownHandler {

    private final Minecraft mc = Minecraft.getMinecraft();
    private final ItemRenderer itemRenderer = new ItemRenderer(this.mc);

    private Entity pointedEntity;

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Pre evt) {

        if (ConfigBuildHandler.classicCombatConfig.removeCooldown && this.mc.player != null) {

            boolean flag = evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS;
            if (flag || evt.getType() == RenderGameOverlayEvent.ElementType.HOTBAR) {

                // disable attack indicator from rendering
                ReflectionHelper.setTicksSinceLastSwing(this.mc.player, (int) Math.ceil(this.mc.player.getCooldownPeriod()));

                // disable attack indicator from rendering when pointing at a living entity
                if (flag) {
                    this.pointedEntity = this.mc.pointedEntity;
                    this.mc.pointedEntity = null;
                }
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderGameOverlay(final RenderGameOverlayEvent.Post evt) {

        if (ConfigBuildHandler.classicCombatConfig.removeCooldown && evt.getType() == RenderGameOverlayEvent.ElementType.CROSSHAIRS) {

            // disable attack indicator from rendering when pointing at a living entity
            this.mc.pointedEntity = this.pointedEntity;
        }
    }

    @SuppressWarnings({"unused", "deprecation"})
    @SubscribeEvent
    public void onItemTooltip(final ItemTooltipEvent evt) {

        // remove attack speed entry for every tooltip containing it
        if (ConfigBuildHandler.classicCombatConfig.removeCooldown) {

            evt.getToolTip().removeIf(line -> line.contains(net.minecraft.util.text.translation.I18n.translateToLocal("attribute.name.generic.attackSpeed")));
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onClientTick(final TickEvent.ClientTickEvent evt) {

        if (ConfigBuildHandler.classicCombatConfig.removeCooldown && evt.phase == TickEvent.Phase.END) {

            if (this.mc.world != null && this.mc.player != null && !this.mc.isGamePaused()) {

                // calculate equipped progress in our own item renderer where it's not reset occasionally
                ReflectionHelper.setTicksSinceLastSwing(this.mc.player, (int) Math.ceil(this.mc.player.getCooldownPeriod()));
                this.itemRenderer.updateEquippedItem();
            }
        }
    }

    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRenderTick(final TickEvent.RenderTickEvent evt) {

        if (ConfigBuildHandler.classicCombatConfig.removeCooldown && evt.phase == TickEvent.Phase.START) {

            this.syncProgress(this.mc.getItemRenderer());
        }
    }

    private void syncProgress(ItemRenderer itemRenderer) {

        ReflectionHelper.setEquippedProgressMainHand(itemRenderer, ReflectionHelper.getEquippedProgressMainHand(this.itemRenderer));
        ReflectionHelper.setPrevEquippedProgressMainHand(itemRenderer, ReflectionHelper.getPrevEquippedProgressMainHand(this.itemRenderer));
        ReflectionHelper.setEquippedProgressOffHand(itemRenderer, ReflectionHelper.getEquippedProgressOffHand(this.itemRenderer));
        ReflectionHelper.setPrevEquippedProgressOffHand(itemRenderer, ReflectionHelper.getPrevEquippedProgressOffHand(this.itemRenderer));
    }

}
