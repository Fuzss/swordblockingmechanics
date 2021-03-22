package com.fuzs.puzzleslib_sbm.client.config;

import com.fuzs.puzzleslib_sbm.config.ConfigManager;
import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import com.google.common.collect.Multimap;
import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Collection;
import java.util.List;

@SuppressWarnings({"NullableProblems", "ConstantConditions"})
public class ConfigScreen extends SettingsScreen {

    private final Multimap<AbstractElement, ConfigOption<?, ?, ?>> modData;
    private OptionsRowList optionsRowList;

    public ConfigScreen(Screen previousScreen, String modId, String modName) {

        super(previousScreen, null, new StringTextComponent(modName));
        this.modData = ConfigManager.get().getAllConfigOptions(modId);
    }

    @Override
    protected void init() {

        this.optionsRowList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.addOptions(this.optionsRowList);
        this.children.add(this.optionsRowList);
        this.addButton(new Button(this.width / 2 - 155 + 160, this.height - 29, 150, 20, DialogTexts.GUI_CANCEL, button -> this.minecraft.displayGuiScreen(this.parentScreen)));
        this.addButton(new Button(this.width / 2 - 155, this.height - 29, 150, 20, DialogTexts.GUI_DONE, button -> {

            this.modData.values().forEach(ConfigOption::onConfirm);
            this.minecraft.displayGuiScreen(this.parentScreen);
        }));
    }

    @Override
    public void render(MatrixStack matrixStack, int mouseX, int mouseY, float partialTicks) {

        this.renderBackground(matrixStack);
        this.optionsRowList.render(matrixStack, mouseX, mouseY, partialTicks);
        drawCenteredString(matrixStack, this.font, this.title, this.width / 2, 5, 16777215);
        super.render(matrixStack, mouseX, mouseY, partialTicks);
        List<IReorderingProcessor> list = func_243293_a(this.optionsRowList, mouseX, mouseY);
        if (list != null) {

            this.renderTooltip(matrixStack, list, mouseX, mouseY);
        }
    }

    private void addOptions(OptionsRowList optionsRowList) {

        for (AbstractElement element : this.modData.keySet()) {

            if (element.getRegistryName().getPath().equals("general")) {

                this.addAllOptions(optionsRowList, this.modData.get(element), element.getDisplayName());
            }
        }

        for (AbstractElement element : this.modData.keySet()) {

            if (!element.getRegistryName().getPath().equals("general")) {

                this.addAllOptions(optionsRowList, this.modData.get(element), element.getDisplayName());
            }
        }
    }

    private void addAllOptions(OptionsRowList optionsRowList, Collection<ConfigOption<?, ?, ?>> elementOptions, String elementName) {

        for (ModConfig.Type type : ModConfig.Type.values()) {

            AbstractOption[] typeOptions = elementOptions.stream().filter(data -> data.isType(type)).toArray(AbstractOption[]::new);
            if (typeOptions.length > 0) {

                optionsRowList.addOption(new NameOption(elementName + " \u2013 " + type));
                optionsRowList.addOptions(typeOptions);
            }
        }
    }

}
