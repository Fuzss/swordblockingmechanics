package com.fuzs.swordblockingcombat.client.handler;

import com.mojang.blaze3d.platform.GlStateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.AbstractClientPlayerEntity;
import net.minecraft.client.renderer.Matrix4f;
import net.minecraft.client.renderer.Quaternion;
import net.minecraft.client.renderer.entity.LivingRenderer;
import net.minecraft.client.renderer.entity.PlayerRenderer;
import net.minecraft.client.renderer.entity.layers.HeldItemLayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.client.renderer.entity.model.PlayerModel;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;

import java.util.List;
import java.util.Map;

public class HeldItemHandler {

    public HeldItemHandler() {

        this.replaceHeldItemLayer();
    }

    private void replaceHeldItemLayer() {

        Map<String, PlayerRenderer> skinMap = Minecraft.getInstance().getRenderManager().getSkinMap();
        for (PlayerRenderer renderer : skinMap.values()) {

            List<LayerRenderer<AbstractClientPlayerEntity, PlayerModel<AbstractClientPlayerEntity>>> layers = ObfuscationReflectionHelper.getPrivateValue(LivingRenderer.class, renderer, "layerRenderers");
            if (layers != null) {

                layers.removeIf(it -> it instanceof HeldItemLayer);
                renderer.addLayer(new SwordBlockingLayer(renderer));
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void applyTransformReverse(net.minecraft.client.renderer.model.ItemTransformVec3f vec, boolean leftHand) {

        if (vec != net.minecraft.client.renderer.model.ItemTransformVec3f.DEFAULT) {

            int i = leftHand ? -1 : 1;
            GlStateManager.scalef(1.0F / vec.scale.getX(), 1.0F / vec.scale.getY(), 1.0F / vec.scale.getZ());
            float x = vec.rotation.getX();
            float y = vec.rotation.getY();
            float z = vec.rotation.getZ();
            if (leftHand) {

                y = -y;
                z = -z;
            }

            Quaternion quat = new Quaternion(x, y, z, true);
            quat.conjugate();
            GlStateManager.multMatrix(new Matrix4f(quat));
            GlStateManager.translatef((float) i * (-vec.translation.getX()), -vec.translation.getY(), -vec.translation.getZ());
        }
    }

}
