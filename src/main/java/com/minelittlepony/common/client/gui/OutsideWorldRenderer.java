package com.minelittlepony.common.client.gui;

import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import org.apache.commons.lang3.ObjectUtils;

/**
 * Utility for rendering objects such as ItemStacks, Entities, and BlockEntities, when there is no client world running.
 * <p>
 * This class performs all the neccessary setup to ensure the above objects render correctly.
 *
 * @author     Sollace
 *
 */
public class OutsideWorldRenderer {
    /**
     * Gets a pre-configured TileEntityRendererDispatcher
     * for rendering BlockEntities outside of the world.
     * <p>
     *
     * @param world An optional World instance to configure the renderer against. May be null.
     *
     * @return a pre-configured TileEntityRendererDispatcher
     */
    public static TileEntityRendererDispatcher configure(@Nullable World world) {
        TileEntityRendererDispatcher dispatcher = TileEntityRendererDispatcher.instance;
        Minecraft mc = Minecraft.getInstance();

        world = ObjectUtils.firstNonNull(dispatcher.world, world, mc.world);

        dispatcher.prepare(world,
                mc.getTextureManager(),
                mc.getRenderManager().getFontRenderer(),
                mc.gameRenderer.getActiveRenderInfo(),
                mc.objectMouseOver);

        mc.getRenderManager().cacheActiveRenderInfo(world,
                mc.gameRenderer.getActiveRenderInfo(),
                mc.pointedEntity);

        return dispatcher;
    }

    /**
     * Renders a ItemStack to the screen.
     *
     * @param stack The stack to render.
     * @param x The left-X position (in pixels)
     * @param y The top-Y position (in pixels)
     */
    public static void renderStack(ItemStack stack, int x, int y) {
        configure(null);
        Minecraft.getInstance().getItemRenderer().renderItemIntoGUI(stack, x, y);
    }
}