package com.minelittlepony.common.client.gui.dimension;

import com.mojang.blaze3d.platform.Window;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;

public class Bounds {
    public int top;
    public int left;
    public int width;
    public int height;

    public static Bounds empty() {
        return new Bounds(0, 0, 0, 0);
    }

    public Bounds(int top, int left, int width, int height) {
        this.top = top;
        this.left = left;
        this.width = width;
        this.height = height;
    }

    public boolean isEmpty() {
        return this.width <= 0 || this.height <= 0;
    }

    public boolean contains(double x, double y) {
        return !this.isEmpty() && x >= (double)this.left && x <= (double)(this.left + this.width) && y >= (double)this.top && y <= (double)(this.top + this.height);
    }

    public Bounds add(Padding other) {
        return new Bounds(this.top - other.top, this.left - other.left, this.width + other.left + other.right, this.height + other.top + other.bottom);
    }

    public Bounds offset(Padding other) {
        return new Bounds(this.top + other.top, this.left + other.left, this.width, this.height);
    }

    public Bounds add(Bounds other) {
        if (other.isEmpty()) {
            return this;
        } else if (this.isEmpty()) {
            return other;
        } else {
            int t = Math.min(this.top, other.top);
            int l = Math.min(this.left, other.left);
            int b = Math.max(this.top + this.height, other.top + other.height);
            int r = Math.max(this.left + this.width, other.left + other.width);
            int h = b - t;
            int w = r - l;
            return new Bounds(t, l, w, h);
        }
    }

    public void copy(Bounds other) {
        this.top = other.top;
        this.left = other.left;
        this.width = other.width;
        this.height = other.height;
    }

    public void draw(PoseStack matrices, int tint) {
        GuiComponent.fill(matrices, this.left, this.top, this.left + this.width, this.top + this.height, tint);
    }

    public void debugMeasure(PoseStack matrices) {
        Window window = Minecraft.getInstance().getWindow();
        GuiComponent.fill(matrices, this.left, 0, this.left + 1, window.getGuiScaledHeight(), -1);
        GuiComponent.fill(matrices, this.left + this.width, 0, this.left + this.width + 1, window.getGuiScaledHeight(), -1);
        GuiComponent.fill(matrices, 0, this.top, window.getGuiScaledWidth(), this.top + 1, -1);
        GuiComponent.fill(matrices, 0, this.top + this.height, window.getGuiScaledWidth(), this.top + this.height + 1, -1);
    }

    protected boolean equals(Bounds o) {
        return this == o || o.top == this.top && o.left == this.left && o.width == this.width && o.height == this.height;
    }

    public boolean equals(Object o) {
        return this == o || o instanceof Bounds && this.equals((Bounds)o);
    }

    public int hashCode() {
        int result = 1;
        result = 31 * result + this.height;
        result = 31 * result + this.left;
        result = 31 * result + this.top;
        result = 31 * result + this.width;
        return result;
    }
}
