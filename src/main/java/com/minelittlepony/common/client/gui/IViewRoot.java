package com.minelittlepony.common.client.gui;

import com.minelittlepony.common.client.gui.dimension.Bounds;
import com.minelittlepony.common.client.gui.dimension.IBounded;
import com.minelittlepony.common.client.gui.dimension.Padding;
import com.minelittlepony.common.event.ScreenInitCallback;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;

public interface IViewRoot extends IBounded, ScreenInitCallback.ButtonList {
    default Bounds getContentBounds() {
        return ((Bounds)this.getAllBounds().stream().reduce(Bounds.empty(), Bounds::add)).add(this.getContentPadding());
    }

    default Set<Bounds> getAllBounds() {
        Set<Bounds> bounds = new HashSet();
        this.getChildElements().forEach((element) -> {
            if (element instanceof IViewRoot) {
                bounds.addAll(((IViewRoot)element).getAllBounds());
            }

            if (element instanceof IBounded) {
                bounds.add(((IBounded)element).getBounds());
            }

        });
        return bounds;
    }

    Padding getContentPadding();

    List<GuiEventListener> getChildElements();

    <T extends GuiEventListener & Widget & NarratableEntry> List<NarratableEntry> buttons();
}
