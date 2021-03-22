package com.fuzs.puzzleslib_sbm.proxy;

import com.fuzs.puzzleslib_sbm.client.config.ConfigOption;
import com.fuzs.puzzleslib_sbm.config.ConfigBuilder;
import com.fuzs.puzzleslib_sbm.config.ConfigManager;
import com.fuzs.puzzleslib_sbm.config.data.IConfigData;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.LogicalSidedProvider;
import net.minecraftforge.fml.config.ModConfig;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collection;
import java.util.stream.Stream;

/**
 * client proxy class
 */
public class ClientProxy implements IProxy<Minecraft> {

    @Override
    public Minecraft getInstance() {

        return LogicalSidedProvider.INSTANCE.get(LogicalSide.CLIENT);
    }

    @SuppressWarnings("ConstantConditions")
    @Nonnull
    @Override
    public PlayerEntity getPlayer(@Nullable PlayerEntity player) {

        return player != null ? player : this.getInstance().player;
    }

    @Override
    public void addGuiFactory(String modId) {

        this.setConfigSpec(modId);
    }

    @SuppressWarnings("ConstantConditions")
    private void setConfigSpec(String modId) {

        ConfigBuilder builder = ConfigManager.builder();
        Stream.of(ModConfig.Type.values()).filter(builder::isSpecBuilt).forEach(type -> {

            ForgeConfigSpec spec = builder.getSpec(type);
            Collection<IConfigData<?, ?>> allData = ConfigManager.get().getAllConfigData(modId, type, false);
            allData.stream()
                    .filter(data -> data instanceof ConfigOption<?, ?, ?>)
                    .map(data -> (ConfigOption<?, ?, ?>) data)
                    .forEach(data -> data.setSpec(spec));
        });
    }

}
