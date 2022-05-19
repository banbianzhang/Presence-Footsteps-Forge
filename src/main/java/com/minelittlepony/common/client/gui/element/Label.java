package com.minelittlepony.common.client.gui.element;

import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.Objects;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;

public class Label extends Button {
    private boolean center;

    public Label(int x, int y) {
        super(x, y);
    }

    public Label setCentered() {
        this.center = true;
        return this;
    }

    public Bounds getBounds() {
        Bounds bounds = super.getBounds();
        Font fonts = Minecraft.getInstance().font;
        bounds.width = fonts.width(this.getStyle().getText());
        if (this.center) {
            bounds.left = this.x - bounds.width / 2;
        }

        return bounds;
    }

    protected boolean isValidClickButton(int button) {
        return false;
    }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return false;
    }

    public void render(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        float var10000 = (float)this.y;
        Objects.requireNonNull(Minecraft.getInstance().font);
        int textY = (int)(var10000 + 9.0F / 1.5F);
        if (this.center) {
            this.drawCenteredLabel(matrices, this.getStyle().getText(), this.x, textY, this.getStyle().getColor(), 0.0);
        } else {
            this.drawLabel(matrices, this.getStyle().getText(), this.x, textY, this.getStyle().getColor(), 0.0);
        }

    }
}
