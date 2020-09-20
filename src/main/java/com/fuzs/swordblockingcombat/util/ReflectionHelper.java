package com.fuzs.swordblockingcombat.util;

import com.fuzs.swordblockingcombat.SwordBlockingCombat;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.entity.RenderLivingBase;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.FoodStats;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;

public class ReflectionHelper {

    private static final String ITEM_RENDERER_EQUIPPED_PROGRESS_MAIN_HAND = "field_187469_f";
    private static final String ITEM_RENDERER_PREV_EQUIPPED_PROGRESS_MAIN_HAND = "field_187470_g";
    private static final String ITEM_RENDERER_EQUIPPED_PROGRESS_OFF_HAND = "field_187471_h";
    private static final String ITEM_RENDERER_PREV_EQUIPPED_PROGRESS_OFF_HAND = "field_187472_i";
    private static final String RENDER_LIVING_BASE_LAYER_RENDERERS = "field_177097_h";
    private static final String ENTITY_LIVING_BASE_TICKS_SINCE_LAST_SWING = "field_184617_aD";
    private static final String ENTITY_PLAYER_FOOD_STATS = "field_71100_bB";
    private static final String FOOD_STATS_FOOD_LEVEL = "field_75127_a";
    private static final String FOOD_STATS_FOOD_SATURATION_LEVEL = "field_75125_b";
    private static final String FOOD_STATS_FOOD_EXHAUSTION_LEVEL = "field_75126_c";
    private static final String FOOD_STATS_FOOD_TIMER = "field_75123_d";

    /* ======================================== GETTER   ===================================== */

    public static float getEquippedProgressMainHand(ItemRenderer instance) {
        return (float) getPrivateValue(ItemRenderer.class, instance, ITEM_RENDERER_EQUIPPED_PROGRESS_MAIN_HAND);
    }

    public static float getPrevEquippedProgressMainHand(ItemRenderer instance) {
        return (float) getPrivateValue(ItemRenderer.class, instance, ITEM_RENDERER_PREV_EQUIPPED_PROGRESS_MAIN_HAND);
    }

    public static float getEquippedProgressOffHand(ItemRenderer instance) {
        return (float) getPrivateValue(ItemRenderer.class, instance, ITEM_RENDERER_EQUIPPED_PROGRESS_OFF_HAND);
    }

    public static float getPrevEquippedProgressOffHand(ItemRenderer instance) {
        return (float) getPrivateValue(ItemRenderer.class, instance, ITEM_RENDERER_PREV_EQUIPPED_PROGRESS_OFF_HAND);
    }

    public static List<LayerRenderer<EntityLivingBase>> getLayerRenderers(RenderPlayer instance) {
        return (List<LayerRenderer<EntityLivingBase>>) getPrivateValue(RenderLivingBase.class, instance, RENDER_LIVING_BASE_LAYER_RENDERERS);
    }

    public static FoodStats getFoodStats(EntityPlayer instance) {
        return (FoodStats) getPrivateValue(EntityPlayer.class, instance, ENTITY_PLAYER_FOOD_STATS);
    }

    public static int getFoodLevel(FoodStats instance) {
        return (int) getPrivateValue(FoodStats.class, instance, FOOD_STATS_FOOD_LEVEL);
    }

    public static float getFoodSaturationLevel(FoodStats instance) {
        return (float) getPrivateValue(FoodStats.class, instance, FOOD_STATS_FOOD_SATURATION_LEVEL);
    }

    public static float getFoodExhaustionLevel(FoodStats instance) {
        return (float) getPrivateValue(FoodStats.class, instance, FOOD_STATS_FOOD_EXHAUSTION_LEVEL);
    }

    public static int getFoodTimer(FoodStats instance) {
        return (int) getPrivateValue(FoodStats.class, instance, FOOD_STATS_FOOD_TIMER);
    }

    /* ======================================== SETTER   ===================================== */

    public static void setEquippedProgressMainHand(ItemRenderer instance, float value) {
        setPrivateValue(ItemRenderer.class, instance, value, ITEM_RENDERER_EQUIPPED_PROGRESS_MAIN_HAND);
    }

    public static void setPrevEquippedProgressMainHand(ItemRenderer instance, float value) {
        setPrivateValue(ItemRenderer.class, instance, value, ITEM_RENDERER_PREV_EQUIPPED_PROGRESS_MAIN_HAND);
    }

    public static void setEquippedProgressOffHand(ItemRenderer instance, float value) {
        setPrivateValue(ItemRenderer.class, instance, value, ITEM_RENDERER_EQUIPPED_PROGRESS_OFF_HAND);
    }

    public static void setPrevEquippedProgressOffHand(ItemRenderer instance, float value) {
        setPrivateValue(ItemRenderer.class, instance, value, ITEM_RENDERER_PREV_EQUIPPED_PROGRESS_OFF_HAND);
    }

    public static void setTicksSinceLastSwing(EntityLivingBase instance, int value) {
        setPrivateValue(EntityLivingBase.class, instance, value, ENTITY_LIVING_BASE_TICKS_SINCE_LAST_SWING);
    }

    public static void setFoodStats(EntityPlayer instance, FoodStats value) {
        setPrivateValue(EntityPlayer.class, instance, value, ENTITY_PLAYER_FOOD_STATS);
    }

    public static void setFoodLevel(FoodStats instance, int value) {
        setPrivateValue(FoodStats.class, instance, value, FOOD_STATS_FOOD_LEVEL);
    }

    public static void setFoodSaturationLevel(FoodStats instance, float value) {
        setPrivateValue(FoodStats.class, instance, value, FOOD_STATS_FOOD_SATURATION_LEVEL);
    }

    public static void setFoodExhaustionLevel(FoodStats instance, float value) {
        setPrivateValue(FoodStats.class, instance, value, FOOD_STATS_FOOD_EXHAUSTION_LEVEL);
    }

    public static void setFoodTimer(FoodStats instance, int value) {
        setPrivateValue(FoodStats.class, instance, value, FOOD_STATS_FOOD_TIMER);
    }

    private static <T> void setPrivateValue(Class<T> clazz, T instance, Object value, String name) {

        try {

            ObfuscationReflectionHelper.setPrivateValue(clazz, instance, value, name);
        } catch (Exception e) {

            SwordBlockingCombat.LOGGER.error("Setting field \"" + name + "\" failed", e);
        }
    }

    private static <T> Object getPrivateValue(Class<T> clazz, T instance, String name) {

        try {

            return ObfuscationReflectionHelper.getPrivateValue(clazz, instance, name);
        } catch (Exception e) {

            SwordBlockingCombat.LOGGER.error("Getting field \"" + name + "\" failed", e);
        }

        return null;
    }

}