package com.minelittlepony.common.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.gui.screen.Screen;

/**
 * Interface element that renders a tooltip when hovered.
 *
 * @author     Sollace
 *
 * @param  <T> The subclass element.
 */
public interface ITooltipped<T extends ITooltipped<T>> {
    /**
     * Draws this element's tooltip.
     */
    void renderToolTip(MatrixStack matrices, Screen parent, int mouseX, int mouseY);
}
