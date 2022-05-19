package com.minelittlepony.common.client.gui.element;

import com.minelittlepony.common.client.gui.IField;
import com.minelittlepony.common.client.gui.Tooltip;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class AbstractSlider<T> extends Button implements IField<T, com.minelittlepony.common.client.gui.element.AbstractSlider<T>> {
    private float min;
    private float max;
    private float value;
    @NotNull
    private IField.@NotNull IChangeCallback<T> action = IField.IChangeCallback::none;
    private @Nullable Function<com.minelittlepony.common.client.gui.element.AbstractSlider<T>, Component> textFunc;
    private @Nullable Function<com.minelittlepony.common.client.gui.element.AbstractSlider<T>, Tooltip> tooltipFunc;

    public AbstractSlider(int x, int y, float min, float max, T value) {
        super(x, y);
        this.min = min;
        this.max = max;
        this.value = convertFromRange(this.valueToFloat(value), min, max);
    }

    protected abstract float valueToFloat(T var1);

    protected abstract T floatToValue(float var1);

    public com.minelittlepony.common.client.gui.element.AbstractSlider<T> onChange(@NotNull IField.@NotNull IChangeCallback<T> action) {
        this.action = action;
        return this;
    }

    public com.minelittlepony.common.client.gui.element.AbstractSlider<T> setTextFormat(@NotNull Function<com.minelittlepony.common.client.gui.element.AbstractSlider<T>, Component> formatter) {
        this.textFunc = formatter;
        this.getStyle().setText((Component)formatter.apply(this));
        return this;
    }

    public com.minelittlepony.common.client.gui.element.AbstractSlider<T> setTooltipFormat(@NotNull Function<com.minelittlepony.common.client.gui.element.AbstractSlider<T>, Tooltip> formatter) {
        this.tooltipFunc = formatter;
        this.getStyle().setTooltip((Tooltip)formatter.apply(this));
        return this;
    }

    public com.minelittlepony.common.client.gui.element.AbstractSlider<T> setValue(T value) {
        this.setClampedValue(convertFromRange(this.valueToFloat(value), this.min, this.max));
        return this;
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (this.active && this.visible && (keyCode == 263 || keyCode == 262)) {
            this.playDownSound(Minecraft.getInstance().getSoundManager());
            float step = (this.max - this.min) / 4.0F;
            if (keyCode == 263) {
                step *= -1.0F;
            }

            this.setClampedValue(this.value + step);
            this.onPress();
            return true;
        } else {
            return false;
        }
    }

    protected void setClampedValue(float value) {
        value = Mth.clamp(value, 0.0F, 1.0F);
        if (value != this.value) {
            float initial = this.value;
            this.value = value;
            this.value = convertFromRange(this.valueToFloat(this.action.perform(this.getValue())), this.min, this.max);
            if (this.value != initial) {
                if (this.textFunc != null) {
                    this.getStyle().setText((Component)this.textFunc.apply(this));
                }

                if (this.tooltipFunc != null) {
                    this.getStyle().setTooltip((Tooltip)this.tooltipFunc.apply(this));
                }
            }
        }

    }

    private void onChange(double mouseX) {
        this.setClampedValue((float)(mouseX - (double)(this.x + 4)) / (float)(this.width - 8));
    }

    public T getValue() {
        return this.floatToValue(convertToRange(this.value, this.min, this.max));
    }

    public void onClick(double mouseX, double mouseY) {
        super.onClick(mouseX, mouseY);
        this.onChange(mouseX);
    }

    protected void onDrag(double mouseX, double mouseY, double mouseDX, double mouseDY) {
        this.onChange(mouseX);
    }

    protected void renderBg(PoseStack matrices, Minecraft mc, int mouseX, int mouseY) {
        mc.getTextureManager().bindForSetup(WIDGETS_LOCATION);
        int i = 46 + (this.isHoveredOrFocused() ? 2 : 1) * 20;
        int sliderX = this.x + (int)(this.value * (float)(this.width - 8));
        this.blit(matrices, sliderX, this.y, 0, i, 4, 20);
        this.blit(matrices, sliderX + 4, this.y, 196, i, 4, 20);
    }

    protected int getYImage(boolean mouseOver) {
        return 0;
    }

    static float convertFromRange(float value, float min, float max) {
        return (Mth.clamp(value, min, max) - min) / (max - min);
    }

    static float convertToRange(float value, float min, float max) {
        return Mth.clamp(min + value * (max - min), min, max);
    }
}
