package com.fuzs.puzzleslib_sbm.client.config;

import com.fuzs.puzzleslib_sbm.config.ConfigManager;
import com.fuzs.puzzleslib_sbm.config.data.IConfigData;
import com.fuzs.puzzleslib_sbm.element.AbstractElement;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.FullscreenResolutionOption;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.DialogTexts;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.SettingsScreen;
import net.minecraft.client.gui.screen.WithNarratorSettingsScreen;
import net.minecraft.client.gui.widget.button.Button;
import net.minecraft.client.gui.widget.list.OptionsRowList;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.fml.config.ModConfig;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class ConfigScreen extends SettingsScreen {

    private OptionsRowList optionsRowList;

    public ConfigScreen(Screen previousScreen, String modId, String modName) {

        super(previousScreen, null, new StringTextComponent(modName));
    }

    @Override
    protected void init() {

        this.optionsRowList = new OptionsRowList(this.minecraft, this.width, this.height, 32, this.height - 32, 25);
        this.optionsRowList.addOption(new FullscreenResolutionOption(this.minecraft.getMainWindow()));
        this.optionsRowList.addOption(AbstractOption.BIOME_BLEND_RADIUS);
        this.optionsRowList.addOptions(OPTIONS);
        this.children.add(this.optionsRowList);
        this.addButton(new Button(this.width / 2 - 100, this.height - 27, 200, 20, DialogTexts.GUI_DONE, (p_213106_1_) -> {
            
            this.minecraft.displayGuiScreen(this.parentScreen);
        }));
    }

//    private static AbstractOption[] getAll(String modId) {
//
//        Collection<IConfigData<?, ?>> client = ConfigManager.get().getAllConfigData(modId, ModConfig.Type.CLIENT, false);
//        Collection<IConfigData<?, ?>> common = ConfigManager.get().getAllConfigData(modId, ModConfig.Type.COMMON, false);
//
//        return Stream.concat(client.stream(), common.stream())
//                .filter(data -> data instanceof ConfigOption<?, ?, ?>)
//                .map(data -> (ConfigOption<?, ?, ?>) data)
//                .toArray(AbstractOption[]::new);
//    }

    private static AbstractOption[] getAll(String modId) {

        List<AbstractOption> list = Lists.newArrayList();
        Multimap<AbstractElement, IConfigData<?, ?>> modData = ConfigManager.get().getAllConfigData(modId);
        for (AbstractElement element : modData.keySet()) {

            for (ModConfig.Type type : ModConfig.Type.values()) {

                modData.get(element).stream().filter(data -> data.isType(type)).forEach(data -> list.add((AbstractOption) data));
            }
        }


        for (Map.Entry<AbstractElement, IConfigData<?, ?>> data : modData.entries()) {

            data.getValue().
        }
    }

}
