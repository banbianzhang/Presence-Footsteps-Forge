package com.minelittlepony.common.client.gui;

import java.util.List;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.renderer.IRenderTypeBuffer;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.util.IReorderingProcessor;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.util.math.vector.Matrix4f;

/**
 * Renders a stylised tooltip with borders and backgrounds.
 *
 * @author     Sollace
 */
public class ToolTipRenderer extends AbstractGui {

    private final Screen screen;

    /***
     * Screates a new tooltip renderer.
     *
     * @param parent The screen containing this element.
     */
    public ToolTipRenderer(Screen parent) {
        screen = parent;
    }

    /**
     * The default font colour of text inside the tooltip.
     */
    protected int getTextColor() {
        return 0xF000F0;
    }

    /**
     * The background fill for the tooltip.
     */
    protected int getFill() {
        return 0xF0100010;
    }

    /**
     * The top (start) gradient colour of the tooltip's border.
     */
    protected int getOutlineGradientTop() {
        return 0x505000FF;
    }

    /**
     * The bottom (end) gradient colour of the tooltip's border.
     * @return
     */
    protected int getOutlineGradientBottom() {
        return 0x5028007F;
    }

    /**
     * Renders a tooltip with text.
     *
     * @param text Text to display.
     * @param x The left X position (in pixels) of the tooltip
     * @param y The top Y position (in pixels) of the tooltip
     */
    public void render(MatrixStack matrices, List<? extends IReorderingProcessor> text, int x, int y) {
        if (text.isEmpty()) {
            return;
        }

        FontRenderer font = Minecraft.getInstance().fontRenderer;
        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();

        RenderSystem.disableRescaleNormal();
        RenderSystem.disableDepthTest();

        int labelWidth = 0;

        for (IReorderingProcessor string : text) {
            labelWidth = Math.max(labelWidth, font.func_243245_a(string));
        }

        int left = x + 12;
        int top = y - 12;
        int labelHeight = 8;

        if (text.size() > 1) {
            labelHeight += 2 + (text.size() - 1) * 10;
        }

        if (left + labelWidth > screen.width) {
            left -= 28 + labelWidth;
        }

        if (top + labelHeight + 6 > screen.height) {
            top = screen.height - labelHeight - 6;
        }

        setBlitOffset(300);
        itemRenderer.zLevel = 300;

        int labelFill = getFill();
        fillGradient(matrices, left - 3,              top - 4,               left + labelWidth + 3, top - 3,               labelFill, labelFill);
        fillGradient(matrices, left - 3,              top + labelHeight + 3, left + labelWidth + 3, top + labelHeight + 4, labelFill, labelFill);
        fillGradient(matrices, left - 3,              top - 3,               left + labelWidth + 3, top + labelHeight + 3, labelFill, labelFill);
        fillGradient(matrices, left - 4,              top - 3,               left - 3,              top + labelHeight + 3, labelFill, labelFill);
        fillGradient(matrices, left + labelWidth + 3, top - 3,               left + labelWidth + 4, top + labelHeight + 3, labelFill, labelFill);

        int borderGradientTop = getOutlineGradientTop();
        int borderGradientBot = getOutlineGradientBottom();
        fillGradient(matrices, left - 3,              top - 3 + 1,           left - 3 + 1,          top + labelHeight + 3 - 1, borderGradientTop, borderGradientBot);
        fillGradient(matrices, left + labelWidth + 2, top - 3 + 1,           left + labelWidth + 3, top + labelHeight + 3 - 1, borderGradientTop, borderGradientBot);
        fillGradient(matrices, left - 3,              top - 3,               left + labelWidth + 3, top - 3 + 1,               borderGradientTop, borderGradientTop);
        fillGradient(matrices, left - 3,              top + labelHeight + 2, left + labelWidth + 3, top + labelHeight + 3,     borderGradientBot, borderGradientBot);

        MatrixStack stack = new MatrixStack();
        IRenderTypeBuffer.Impl immediate = IRenderTypeBuffer.getImpl(Tessellator.getInstance().getBuffer());

        stack.translate(0, 0, itemRenderer.zLevel);
        Matrix4f matrix = stack.getLast().getMatrix();

        int color = getTextColor();

        for(int r = 0; r < text.size(); ++r) {
            IReorderingProcessor line = text.get(r);
            if (line != null) {
                font.func_238416_a_(line, left, top, -1, true, matrix, immediate, true, 0, color);
            }

            if (r == 0) {
                top += 2;
            }

            top += 10;
        }

        immediate.finish();

        setBlitOffset(0);
        itemRenderer.zLevel = 0;
        RenderSystem.enableDepthTest();
        RenderSystem.enableRescaleNormal();
    }
}