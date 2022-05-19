package com.minelittlepony.common.client.gui.dimension;

public class Padding {
    public int top;
    public int left;
    public int bottom;
    public int right;

    public Padding(int top, int left, int bottom, int right) {
        this.top = top;
        this.left = left;
        this.bottom = bottom;
        this.right = right;
    }

    public void setAll(int padding) {
        this.setVertical(padding);
        this.setHorizontal(padding);
    }

    public void setVertical(int padding) {
        this.top = padding;
        this.bottom = padding;
    }

    public void setHorizontal(int padding) {
        this.left = padding;
        this.right = padding;
    }
}
