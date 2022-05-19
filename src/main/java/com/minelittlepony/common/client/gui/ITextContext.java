package com.minelittlepony.common.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.Tesselator;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.FormattedText;
import net.minecraft.util.FormattedCharSequence;

import java.util.Iterator;

public interface ITextContext {
    default Font getFont() {
        return Minecraft.getInstance().font;
    }

    default void drawLabel(PoseStack matrices, Component text, int x, int y, int color, double zIndex) {
        MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
        matrices.translate(0.0, 0.0, zIndex);
        this.getFont().drawInBatch(text, (float)x, (float)y, color, true, matrices.last().pose(), immediate, true, 0, 15728880);
        immediate.endBatch();
    }

    default void drawCenteredLabel(PoseStack matrices, Component text, int x, int y, int color, double zIndex) {
        int width = this.getFont().width(text);
        this.drawLabel(matrices, text, x - width / 2, y, color, zIndex);
    }

    default void drawTextBlock(PoseStack matrices, FormattedText text, int x, int y, int maxWidth, int color) {
        for(Iterator var7 = this.getFont().split(text, maxWidth).iterator(); var7.hasNext(); y += 9) {
            FormattedCharSequence line = (FormattedCharSequence)var7.next();
            float left = (float)x;
            MultiBufferSource.BufferSource immediate = MultiBufferSource.immediate(Tesselator.getInstance().getBuilder());
            this.getFont().drawInBatch(line, left, (float)y, color, false, matrices.last().pose(), immediate, true, 0, 15728880);
            immediate.endBatch();
        }

    }
}
