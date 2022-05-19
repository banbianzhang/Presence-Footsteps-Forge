package com.minelittlepony.common.client.gui.style;

import com.minelittlepony.common.client.gui.Tooltip;
import com.minelittlepony.common.client.gui.sprite.ISprite;
import com.minelittlepony.common.client.gui.sprite.ItemStackSprite;
import java.util.List;
import java.util.Optional;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class Style {
    private ISprite icon;
    public int toolTipX;
    public int toolTipY;
    private Optional<Tooltip> tooltip;
    private Component text;
    private int color;

    public Style() {
        this.icon = ISprite.EMPTY;
        this.toolTipX = 0;
        this.toolTipY = 0;
        this.tooltip = Optional.empty();
        this.text = TextComponent.EMPTY;
        this.color = -1;
    }

    public ISprite getIcon() {
        return this.icon;
    }

    public boolean hasIcon() {
        return this.getIcon() != ISprite.EMPTY;
    }

    public Style setColor(int color) {
        this.color = color;
        return this;
    }

    public int getColor() {
        return this.color;
    }

    public Style setText(String text) {
        return this.setText((Component)(new TranslatableComponent(text)));
    }

    public Style setText(Component text) {
        this.text = text;
        return this;
    }

    public Component getText() {
        return this.text;
    }

    public Style setIcon(ItemLike iitem) {
        return this.setIcon((ISprite)(new ItemStackSprite()).setStack(iitem));
    }

    public Style setIcon(ItemStack stack) {
        return this.setIcon((ISprite)(new ItemStackSprite()).setStack(stack));
    }

    public Style setIcon(ISprite sprite) {
        this.icon = sprite;
        return this;
    }

    public Style setIcon(ItemStack stack, int colour) {
        return this.setIcon((ISprite)(new ItemStackSprite()).setStack(stack).setTint(colour));
    }

    public Style setTooltip(String tooltip) {
        return this.setTooltip(Tooltip.of(tooltip));
    }

    public Style setTooltip(Component tooltip) {
        return this.setTooltip(Tooltip.of(tooltip));
    }

    public Style setTooltip(String tooltip, int x, int y) {
        return this.setTooltip(tooltip).setTooltipOffset(x, y);
    }

    public Style setTooltip(Component tooltip, int x, int y) {
        return this.setTooltip(tooltip).setTooltipOffset(x, y);
    }

    public Style setTooltip(List<Component> tooltip) {
        return this.setTooltip(Tooltip.of(tooltip));
    }

    public Style setTooltip(Tooltip tooltip) {
        this.tooltip = Optional.of(tooltip);
        return this;
    }

    public Optional<Tooltip> getTooltip() {
        return this.tooltip;
    }

    public Style setTooltipOffset(int x, int y) {
        this.toolTipX = x;
        this.toolTipY = y;
        return this;
    }
}
