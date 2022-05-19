package com.minelittlepony.common.client.gui.element;

public class Slider extends AbstractSlider<Float> {
    public Slider(int x, int y, float min, float max, float value) {
        super(x, y, min, max, value);
    }

    protected float valueToFloat(Float value) {
        return value;
    }

    protected Float floatToValue(float value) {
        return value;
    }
}
