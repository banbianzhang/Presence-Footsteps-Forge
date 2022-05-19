package com.minelittlepony.common.client.gui.sprite;

import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.common.client.gui.dimension.IBounded;
import com.mojang.blaze3d.vertex.PoseStack;

public interface ISprite extends IBounded {
    ISprite EMPTY = (m, x, y, mx, my, t) -> {
    };

    void render(PoseStack var1, int var2, int var3, int var4, int var5, float var6);

    default Bounds getBounds() {
        return Bounds.empty();
    }

    default void setBounds(Bounds bounds) {
    }
}
