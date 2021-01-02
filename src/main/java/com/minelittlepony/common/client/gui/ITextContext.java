package com.minelittlepony.common.client.gui;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.util.IReorderingProcessor;
import net.minecraft.util.math.vector.TransformationMatrix;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextProperties;

/**
 * Context utility for things that want to render text to the screen.
 * <p>
 * These methods are provided as an alternative to the Vanilla ones,
 * with one slight change to allow text to be rendered over content
 * that would normally be layered on top of it.
 * <p>
 * If you have entities in your screen and can't see text behind them,
 * use this.
 *
 * @author     Sollace
 */
public interface ITextContext {

    /**
     * Gets the global TextRenderer instance.
     */
    default FontRenderer getFont() {
        return Minecraft.getInstance().fontRenderer;
    }

    /**
     * Draws a piece of coloured, left-aligned text to the screen.
     *
     * @param text The text to render
     * @param x The left X position (in pixel)
     * @param y The top Y position (in pixel)
     * @param color The font colour
     * @param zIndex The Z-index used when layering multiple elements.
     */
    default void drawLabel(MatrixStack matrices, ITextComponent text, int x, int y, int color, double zIndex) {
        IRenderTypeBuffer.Impl immediate = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
        matrices.translate(0, 0, zIndex);
        getFont().func_243247_a(text, x, y, color, true, matrices.getLast().getMatrix(), immediate, true, 0, 0xF000F0);
        immediate.finish();
    }

    /**
     * Draws a piece of coloured, centered text to the screen.
     *
     * @param text The text to render
     * @param x The left X position (in pixel)
     * @param y The top Y position (in pixel)
     * @param color The font colour
     * @param zIndex The Z-index used when layering multiple elements.
     */
    default void drawCenteredLabel(MatrixStack matrices, ITextComponent text, int x, int y, int color, double zIndex) {
        int width = getFont().getStringPropertyWidth(text);

        drawLabel(matrices, text, x - width/2, y, color, zIndex);
    }

    /**
     * Draws a block of text spanning multiple lines. Content is left-aligned,
     * and wrapped to fit in the given page width.
     *
     * @param text The text to render
     * @param x The left X position (in pixel)
     * @param y The top Y position (in pixel)
     * @param maxWidth The maximum page width
     * @param color The font colour
     */
    default void drawTextBlock(MatrixStack matrices, ITextProperties text, int x, int y, int maxWidth, int color) {
        TransformationMatrix.identity().getMatrix();

        for (IReorderingProcessor line : getFont().trimStringToWidth(text, maxWidth)) {
            float left = x;
            IRenderTypeBuffer.Impl immediate = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());
            getFont().func_238416_a_(line, left, y, color, false, matrices.getLast().getMatrix(), immediate, true, 0, 0xF000F0);
            immediate.finish();

            y += 9;
        }
    }
}
