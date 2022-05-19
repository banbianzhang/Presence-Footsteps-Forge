package com.minelittlepony.common.client.gui.element;

import com.minelittlepony.common.client.gui.ITextContext;
import com.minelittlepony.common.client.gui.ITooltipped;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.common.client.gui.dimension.IBounded;
import com.minelittlepony.common.client.gui.style.IStyled;
import com.minelittlepony.common.client.gui.style.Style;
import com.mojang.blaze3d.platform.GlStateManager.DestFactor;
import com.mojang.blaze3d.platform.GlStateManager.SourceFactor;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

public class Button extends AbstractButton implements ITooltipped<Button>, IBounded, ITextContext, IStyled<Button> {
    private Style style;
    private final Bounds bounds;
    private static final Consumer<Button> NONE = (v) -> {
    };
    private @NotNull Consumer<Button> action;

    public Button(int x, int y) {
        this(x, y, 200, 20);
    }

    public Button(int x, int y, int width, int height) {
        super(x, y, width, height, TextComponent.EMPTY);
        this.style = new Style();
        this.action = NONE;
        this.bounds = new Bounds(y, x, width, height);
    }

    public Button onClick(@NotNull Consumer<? extends Button> callback) {
        this.action = (Consumer<Button>) callback;
        return this;
    }

    public Button setEnabled(boolean enable) {
        this.active = enable;
        return this;
    }

    public Style getStyle() {
        return this.style;
    }

    public Button setStyle(Style style) {
        this.style = style;
        return this;
    }

    public Bounds getBounds() {
        return this.bounds;
    }

    public void setBounds(Bounds bounds) {
        this.bounds.copy(bounds);
        this.x = bounds.left;
        this.y = bounds.top;
        this.width = bounds.width;
        this.height = bounds.height;
    }

    public void updateNarration(NarrationElementOutput narrationMsg) {
        this.defaultButtonNarrationText(narrationMsg);
        this.getStyle().getTooltip().ifPresent((tooltip) -> {
            tooltip.appendNarrations(narrationMsg);
        });
    }

    public void onPress() {
        this.action.accept(this);
    }

    public void renderToolTip(PoseStack matrices, Screen parent, int mouseX, int mouseY) {
        if (this.visible) {
            this.getStyle().getTooltip().ifPresent((tooltip) -> {
                parent.renderComponentTooltip(matrices, tooltip.getLines(), mouseX + this.getStyle().toolTipX, mouseY + this.getStyle().toolTipY);
            });
        }

    }

    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, this.alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA);
        int state = 46 + this.getYImage(this.isHoveredOrFocused()) * 20;
        this.renderButtonBlit(matrices, this.x, this.y, state, this.width, this.height);
        this.renderBg(matrices, mc, mouseX, mouseY);
        int foreColor = this.getStyle().getColor();
        if (!this.active) {
            foreColor = 10526880;
        } else if (this.isHoveredOrFocused()) {
            foreColor = 16777120;
        }

        if (this.getStyle().hasIcon()) {
            this.getStyle().getIcon().render(matrices, this.x, this.y, mouseX, mouseY, partialTicks);
        }

        this.setMessage(this.getStyle().getText());
        this.renderForground(matrices, mc, mouseX, mouseY, foreColor | Mth.ceil(this.alpha * 255.0F) << 24);
    }

    protected void renderForground(PoseStack matrices, Minecraft mc, int mouseX, int mouseY, int foreColor) {
        this.drawCenteredLabel(matrices, this.getMessage(), this.x + this.width / 2, this.y + (this.height - 8) / 2, foreColor, 0.0);
    }

    protected final void renderButtonBlit(PoseStack matrices, int x, int y, int state, int blockWidth, int blockHeight) {
        int endV = 200 - blockWidth / 2;
        int endU = state + 20 - blockHeight / 2;
        this.blit(matrices, x, y, 0, state, blockWidth / 2, blockHeight / 2);
        this.blit(matrices, x + blockWidth / 2, y, endV, state, blockWidth / 2, blockHeight / 2);
        this.blit(matrices, x, y + blockHeight / 2, 0, endU, blockWidth / 2, blockHeight / 2);
        this.blit(matrices, x + blockWidth / 2, y + blockHeight / 2, endV, endU, blockWidth / 2, blockHeight / 2);
    }
}
