package com.minelittlepony.common.client.gui.element;

import java.util.function.Consumer;

import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.AbstractButton;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.util.Mth;
import org.jetbrains.annotations.NotNull;

import com.minelittlepony.common.client.gui.ITextContext;
import com.minelittlepony.common.client.gui.ITooltipped;
import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.common.client.gui.dimension.IBounded;
import com.minelittlepony.common.client.gui.style.IStyled;
import com.minelittlepony.common.client.gui.style.Style;
import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;

/**
 * A stylable button element.
 * <p>
 * All appearance other than dimensions and position are controlled by this element's {Style}
 * to make switching and changing styles easier.
 *
 * @author     Sollace
 *
 */
public class Button extends AbstractButton implements ITooltipped<Button>, IBounded, ITextContext, IStyled<Button> {

    private Style style = new Style();

    private final Bounds bounds;

    private static final Consumer<Button> NONE = v -> {};
    @NotNull
    private Consumer<Button> action = NONE;

    private boolean wasHovered;

    public Button(int x, int y) {
        this(x, y, 200, 20);
    }

    public Button(int x, int y, int width, int height) {
        super(x, y, width, height, CommonComponents.EMPTY);

        bounds = new Bounds(y, x, width, height);
    }

    /**
     * Adds a listener to call when this button is clicked.
     *
     * @param callback The callback function.
     * @return {@code this} for chaining purposes.
     */
    @SuppressWarnings("unchecked")
    public Button onClick(@NotNull Consumer<? extends Button> callback) {
        action = (Consumer<Button>)callback;

        return this;
    }

    /**
     * Enables or disables this button.
     */
    public Button setEnabled(boolean enable) {
        active = enable;
        return this;
    }

    /**
     * Hides or shows this button.
     */
    public Button setVisible(boolean visible) {
        this.visible = visible;
        return this;
    }

    /**
     * Gets this button's current styling.
     */
    @Override
    public Style getStyle() {
        return style;
    }

    /**
     * Sets this button's current styling.
     */
    @Override
    public Button setStyle(Style style) {
        this.style = style;

        return this;
    }

    @Override
    public Bounds getBounds() {
        return bounds;
    }

    @Override
    public void setBounds(Bounds bounds) {
        this.bounds.copy(bounds);

        x = bounds.left;
        y = bounds.top;
        width = bounds.width;
        height = bounds.height;
    }

    @Override
    public void updateNarration(NarrationElementOutput narrationMsg) {
        defaultButtonNarrationText(narrationMsg);
        getStyle().getTooltip().ifPresent(tooltip -> tooltip.appendNarrations(narrationMsg));
    }

    @Override
    public void onPress() {
        action.accept(this);
    }

    @Override
    public void renderToolTip(PoseStack matrices, Screen parent, int mouseX, int mouseY) {
        final boolean hovered = this.isHoveredOrFocused();

        if (hovered != wasHovered) {
            wasHovered = hovered;
        }

        if (hovered && visible) {
            getStyle().getTooltip().ifPresent(tooltip -> {
                parent.renderComponentTooltip(matrices, tooltip.getLines(), mouseX + getStyle().toolTipX, mouseY + getStyle().toolTipY);
            });
        }
    }

    @Override
    public void renderButton(PoseStack matrices, int mouseX, int mouseY, float partialTicks) {
        Minecraft mc = Minecraft.getInstance();

        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.setShaderTexture(0, WIDGETS_LOCATION);
        RenderSystem.setShaderColor(1, 1, 1, alpha);
        RenderSystem.enableBlend();
        RenderSystem.defaultBlendFunc();
        RenderSystem.blendFunc(
                GlStateManager.SourceFactor.SRC_ALPHA,
                GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

        int state = 46 + getYImage(isHoveredOrFocused()) * 20;

        renderButtonBlit(matrices, x, y, state, width, height);

        renderBg(matrices, mc, mouseX, mouseY);

        int foreColor = getStyle().getColor();
        if (!active) {
            foreColor = 10526880;
        } else if (isHoveredOrFocused()) {
            foreColor = 16777120;
        }

        if (getStyle().hasIcon()) {
            getStyle().getIcon().render(matrices, x, y, mouseX, mouseY, partialTicks);
        }

        setMessage(getStyle().getText());
        renderForground(matrices, mc, mouseX, mouseY, foreColor | Mth.ceil(alpha * 255.0F) << 24);
    }

    protected void renderForground(PoseStack matrices, Minecraft mc, int mouseX, int mouseY, int foreColor) {
        drawCenteredLabel(matrices, getMessage(), x + width / 2, y + (height - 8) / 2, foreColor, 0);
    }

    protected final void renderButtonBlit(PoseStack matrices, int x, int y, int state, int blockWidth, int blockHeight) {

        int endV = 200 - blockWidth/2;
        int endU = state + 20 - blockHeight/2;

        blit(matrices,
                x,                y,
                0, state,
                blockWidth/2, blockHeight/2);
        blit(matrices,
                x + blockWidth/2, y,
                endV, state,
                blockWidth/2, blockHeight/2);

        blit(matrices,
                x,                y + blockHeight/2,
                0, endU,
                blockWidth/2, blockHeight/2);
        blit(matrices,
                x + blockWidth/2, y + blockHeight/2,
                endV, endU,
                blockWidth/2, blockHeight/2);
    }
}
