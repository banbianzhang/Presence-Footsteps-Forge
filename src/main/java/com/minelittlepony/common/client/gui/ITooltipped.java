package com.minelittlepony.common.client.gui;

import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.client.gui.screens.Screen;

public interface ITooltipped<T extends ITooltipped<T>> {
    void renderToolTip(PoseStack var1, Screen var2, int var3, int var4);
}