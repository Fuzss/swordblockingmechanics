package com.fuzs.puzzleslib_sbm.client.config;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;

@SuppressWarnings("NullableProblems")
public class NameOption extends AbstractOption {

    private final ITextComponent name;

    public NameOption(String name) {

        super(name);
        // just like game rules
        this.name = new StringTextComponent(name).mergeStyle(TextFormatting.BOLD, TextFormatting.YELLOW);
    }

    @Override
    protected final ITextComponent getBaseMessageTranslation() {

        return this.name;
    }

    @Override
    public Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn) {

        return new Button(xIn, yIn, widthIn, 20, this.getBaseMessageTranslation(), button -> {}) {

            @Override
            public void renderButton(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

                FontRenderer fontrenderer = Minecraft.getInstance().fontRenderer;
                RenderSystem.color4f(1.0F, 1.0F, 1.0F, this.alpha);
                RenderSystem.enableBlend();
                RenderSystem.defaultBlendFunc();
                RenderSystem.enableDepthTest();
                drawCenteredString(matrixStack, fontrenderer, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, this.getFGColor() | MathHelper.ceil(this.alpha * 255.0F) << 24);
            }

            @Override
            public boolean mouseClicked(double mouseX, double mouseY, int button) {

                return false;
            }

            @Override
            public boolean keyPressed(int keyCode, int scanCode, int modifiers) {

                return false;
            }

        };
    }

}
