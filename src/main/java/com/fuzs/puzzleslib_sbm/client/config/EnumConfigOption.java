package com.fuzs.puzzleslib_sbm.client.config;

import net.minecraft.client.gui.widget.Widget;
import net.minecraft.client.gui.widget.button.OptionButton;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;
import org.apache.commons.lang3.ArrayUtils;

import java.util.function.Consumer;
import java.util.function.Function;

public class EnumConfigOption<T extends Enum<T>> extends ConfigOption<ForgeConfigSpec.EnumValue<T>, T, T> {

    public EnumConfigOption(ForgeConfigSpec.EnumValue<T> configValue, ModConfig.Type configType, Consumer<T> syncToField, Function<T, T> transformValue) {

        super(configValue, configType, syncToField, transformValue);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Widget createWidget(int xIn, int yIn, int widthIn) {

        return new OptionButton(xIn, yIn, widthIn, 20, this, this.getMessage(), button -> {

            this.modify(value -> {

                T[] allValues = (T[]) this.get().getClass().getEnumConstants();
                int index = ArrayUtils.indexOf(allValues, value);
                index = ++index % allValues.length;
                return allValues[index];
            });

            this.setMessage(button);
        });
    }

    @Override
    protected ITextComponent getMessage(T value) {

        return this.getGenericValueComponent(new StringTextComponent(value.toString()));
    }

}
