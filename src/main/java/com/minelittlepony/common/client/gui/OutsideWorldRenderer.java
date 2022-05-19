package com.minelittlepony.common.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderDispatcher;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.apache.commons.lang3.ObjectUtils;
import org.jetbrains.annotations.Nullable;

public class OutsideWorldRenderer {
    public OutsideWorldRenderer() {
    }

    public static BlockEntityRenderDispatcher configure(@Nullable Level world) {
        Minecraft mc = Minecraft.getInstance();
        BlockEntityRenderDispatcher dispatcher = mc.getBlockEntityRenderDispatcher();
        world = (Level)ObjectUtils.firstNonNull(new Level[]{dispatcher.level, world, mc.level});
        dispatcher.prepare(world, mc.gameRenderer.getMainCamera(), mc.hitResult);
        mc.getEntityRenderDispatcher().prepare(world, mc.gameRenderer.getMainCamera(), mc.crosshairPickEntity);
        return dispatcher;
    }

    public static void renderStack(ItemStack stack, int x, int y) {
        configure((Level)null);
        Minecraft.getInstance().getItemRenderer().renderGuiItem(stack, x, y);
    }
}
