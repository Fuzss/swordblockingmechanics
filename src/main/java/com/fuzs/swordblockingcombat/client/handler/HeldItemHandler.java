package com.fuzs.swordblockingcombat.client.handler;

import com.fuzs.swordblockingcombat.util.ReflectionHelper;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerHeldItem;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.util.vector.Quaternion;

import java.util.List;
import java.util.Map;

public class HeldItemHandler {

    public static void replaceHeldItemLayer() {

        Map<String, RenderPlayer> skinMap = Minecraft.getMinecraft().getRenderManager().getSkinMap();
        for (RenderPlayer renderPlayer : skinMap.values()) {

            List<LayerRenderer<EntityLivingBase>> layers = ReflectionHelper.getLayerRenderers(renderPlayer);
            if (layers != null) {

                layers.removeIf(it -> it instanceof LayerHeldItem);
                renderPlayer.addLayer(new SwordBlockingLayer(renderPlayer));
            }
        }
    }

    @SuppressWarnings("deprecation")
    public static void applyTransformReverse(net.minecraft.client.renderer.block.model.ItemTransformVec3f vec, boolean leftHand) {

        if (vec != net.minecraft.client.renderer.block.model.ItemTransformVec3f.DEFAULT) {

            int i = leftHand ? -1 : 1;
            GlStateManager.scale(1.0F / vec.scale.x, 1.0F / vec.scale.y, 1.0F / vec.scale.z);
            float x = vec.rotation.x;
            float y = vec.rotation.y;
            float z = vec.rotation.z;

            if (leftHand) {

                y = -y;
                z = -z;
            }

            Quaternion quat = makeQuaternion(x, y, z);
            GlStateManager.rotate(quat.negate(quat));
            GlStateManager.translate((float) i * (-vec.translation.x), -vec.translation.y, -vec.translation.z);
        }
    }

    private static Quaternion makeQuaternion(float p_188035_0_, float p_188035_1_, float p_188035_2_) {

        float f = p_188035_0_ * 0.017453292F;
        float f1 = p_188035_1_ * 0.017453292F;
        float f2 = p_188035_2_ * 0.017453292F;
        float f3 = MathHelper.sin(0.5F * f);
        float f4 = MathHelper.cos(0.5F * f);
        float f5 = MathHelper.sin(0.5F * f1);
        float f6 = MathHelper.cos(0.5F * f1);
        float f7 = MathHelper.sin(0.5F * f2);
        float f8 = MathHelper.cos(0.5F * f2);

        return new Quaternion(f3 * f6 * f8 + f4 * f5 * f7, f4 * f5 * f8 - f3 * f6 * f7, f3 * f5 * f8 + f4 * f6 * f7, f4 * f6 * f8 - f3 * f5 * f7);
    }

}
