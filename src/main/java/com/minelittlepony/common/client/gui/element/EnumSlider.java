package com.minelittlepony.common.client.gui.element;

import net.minecraft.network.chat.Component;

public class EnumSlider<T extends Enum<T>> extends AbstractSlider<T> {
    private final T[] values;

    public EnumSlider(int x, int y, T value) {
        super(x, y, 0.0F, (float)(((Enum[])value.getClass().getEnumConstants()).length - 1), value);
        this.values = (T[]) value.getClass().getEnumConstants();
        this.setTextFormat((s) -> {
            return Component.nullToEmpty(((Enum)this.getValue()).name());
        });
    }

    protected float valueToFloat(T value) {
        return (float)value.ordinal();
    }

    protected T floatToValue(float value) {
        for(value = (float)Math.round(value); value < 0.0F; value += (float)this.values.length) {
        }

        return this.values[(int)value % this.values.length];
    }
}