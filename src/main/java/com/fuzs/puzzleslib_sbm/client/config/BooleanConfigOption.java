package com.fuzs.puzzleslib_sbm.client.config;

import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.function.Consumer;
import java.util.function.Function;

public class BooleanConfigOption extends ConfigOption<ForgeConfigSpec.BooleanValue, Boolean, Boolean> {

    public BooleanConfigOption(ForgeConfigSpec.BooleanValue configValue, ModConfig.Type configType, Consumer<Boolean> syncToField, Function<Boolean, Boolean> transformValue) {

        super(configValue, configType, syncToField, transformValue);
    }

    @Override
    protected Widget createWidget(int xIn, int yIn, int widthIn) {

        return new OptionButton(xIn, yIn, widthIn, 20, this, this.getMessage(), button -> {

            this.advanceButton(value -> !value);
            this.setMessage(button);
        });
    }

    @Override
    protected ITextComponent getMessage(Boolean value) {

        return DialogTexts.getComposedOptionMessage(this.getBaseMessageTranslation(), value);
    }

}
