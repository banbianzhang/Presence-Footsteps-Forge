package com.minelittlepony.common.client.gui;

import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.common.client.gui.dimension.Padding;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

import java.util.List;

interface IViewRootDefaultImpl extends IViewRoot {
    default Bounds getBounds() {
        throw new RuntimeException("stub");
    }

    default void setBounds(Bounds bounds) {
        throw new RuntimeException("stub");
    }

    default Padding getContentPadding() {
        throw new RuntimeException("stub");
    }

    default <T extends GuiEventListener & Widget & NarratableEntry> List<NarratableEntry> buttons() {
        throw new RuntimeException("stub");
    }

    default <T extends GuiEventListener & Widget & NarratableEntry> T addButton(T button) {
        throw new RuntimeException("stub");
    }

    default List<GuiEventListener> getChildElements() {
        throw new RuntimeException("stub");
    }
}
