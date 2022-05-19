package com.minelittlepony.common.client.gui.sprite;

import com.minelittlepony.common.client.gui.OutsideWorldRenderer;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ItemLike;

public class ItemStackSprite implements ISprite {
    private ItemStack stack;
    private int tint;

    public ItemStackSprite() {
        this.stack = ItemStack.EMPTY;
        this.tint = -1;
    }

    public ItemStackSprite setStack(ItemLike iitem) {
        return this.setStack(new ItemStack(iitem));
    }

    public ItemStackSprite setStack(ItemStack stack) {
        this.stack = stack;
        return this.setTint(this.tint);
    }

    public ItemStackSprite setTint(int tint) {
        this.stack.getOrCreateTagElement("display").putInt("color", tint);
        return this;
    }

    public void render(PoseStack matrices, int x, int y, int mouseX, int mouseY, float partialTicks) {
        OutsideWorldRenderer.renderStack(this.stack, x + 2, y + 2);
        RenderSystem.disableDepthTest();
    }
}
