package com.fuzs.puzzleslib_sbm.client.config;


import com.fuzs.puzzleslib_sbm.config.data.IConfigData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import net.minecraft.client.AbstractOption;
import net.minecraft.client.GameSettings;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.widget.Widget;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.config.ModConfig;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.UnaryOperator;

/**
 * internal storage for registered config entries
 * @param <S> config value of a certain type
 * @param <T> type for value
 * @param <R> return type after applying transformer
 */
@SuppressWarnings("NullableProblems")
public abstract class ConfigOption<S extends ForgeConfigSpec.ConfigValue<T>, T, R> extends AbstractOption implements IConfigData<T, R> {

    /**
     * config value entry
     */
    protected final S configValue;
    /**
     * value spec for context
     */
    protected ForgeConfigSpec.ValueSpec valueSpec;
    private T currentValue;
    /**
     * name of this for config screen
     */
    private final ITextComponent name;
    /**
     * config path for value
     */
    private final String path;
    /**
     * config type of this entry
     */
    public final ModConfig.Type type;
    /**
     * action to perform when the entry is updated
     */
    private final Consumer<R> syncToField;
    /**
     * transformation to apply when returning value, usually {@link Function#identity}
     */
    private final Function<T, R> transformValue;

    /**
     * new entry storage
     */
    public ConfigOption(S configValue, ModConfig.Type configType, Consumer<R> syncToField, Function<T, R> transformValue) {

        super("");
        this.name = new StringTextComponent(Iterables.getLast(configValue.getPath()));
        this.path = String.join(".", configValue.getPath());
        this.configValue = configValue;
        this.type = configType;
        this.syncToField = syncToField;
        this.transformValue = transformValue;
    }

    @Override
    public final ITextComponent getBaseMessageTranslation() {

        return this.name;
    }

    @Override
    public R get() {

        return this.transformValue.apply(this.getRaw());
    }

    @Override
    public T getRaw() {

        return this.configValue.get();
    }

    @Override
    public void sync() {

        this.syncToField.accept(this.get());
    }

    @Override
    public void modify(UnaryOperator<T> operator) {

        this.configValue.set(operator.apply(this.getRaw()));
        this.sync();
    }

    protected void advanceButton(UnaryOperator<T> operator) {

        this.currentValue = operator.apply(this.currentValue);
    }

    public void onConfirm() {

        if (this.currentValue != this.getRaw()) {

            this.configValue.set(this.currentValue);
            this.sync();
        }
    }

    @Override
    public boolean isAtPath(String path) {

        return this.path.equals(path);
    }

    @Override
    public ModConfig.Type getType() {

        return this.type;
    }

    public void setSpec(ForgeConfigSpec spec) {

        Object valueAtPath = spec.getRaw(this.configValue.getPath());
        if (valueAtPath instanceof ForgeConfigSpec.ValueSpec) {

            this.valueSpec = (ForgeConfigSpec.ValueSpec) valueAtPath;
        }
    }

    private void createComment(FontRenderer fontRenderer) {

        String fullComment = this.valueSpec.getComment();
        String[] splitComment = fullComment.split("\n");
        List<IReorderingProcessor> values = Lists.newArrayList();
        for (String comment : this.processComment(splitComment)) {

            values.addAll(fontRenderer.trimStringToWidth(new StringTextComponent(comment), 200));
        }

        this.setOptionValues(ImmutableList.copyOf(values));
    }

    protected String[] processComment(String[] splitComment) {

        return splitComment;
    }

    @Override
    public final Widget createWidget(GameSettings options, int xIn, int yIn, int widthIn) {

        this.currentValue = this.getRaw();
        this.createComment(Minecraft.getInstance().fontRenderer);
        return this.createWidget(xIn, yIn, widthIn);
    }

    protected final void setMessage(Widget widget) {

        widget.setMessage(this.getMessage(this.currentValue));
    }

    protected final ITextComponent getMessage() {

        return this.getMessage(this.currentValue);
    }

    protected abstract Widget createWidget(int xIn, int yIn, int widthIn);

    protected abstract ITextComponent getMessage(T value);

}
