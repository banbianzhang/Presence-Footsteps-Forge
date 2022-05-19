package com.minelittlepony.common.event;

//import net.fabricmc.fabric.api.event.Event;
//import net.fabricmc.fabric.api.event.EventFactory;
import net.minecraft.client.gui.components.Widget;
import net.minecraft.client.gui.components.events.GuiEventListener;
import net.minecraft.client.gui.narration.NarratableEntry;
import net.minecraft.client.gui.screens.Screen;

@FunctionalInterface
public interface ScreenInitCallback {
//    Event<ScreenInitCallback> EVENT = EventFactory.createArrayBacked(ScreenInitCallback.class, (listeners) -> {
//        return (screen, buttons) -> {
//            ScreenInitCallback[] var3 = listeners;
//            int var4 = listeners.length;
//
//            for(int var5 = 0; var5 < var4; ++var5) {
//                ScreenInitCallback event = var3[var5];
//                event.init(screen, buttons);
//            }
//
//        };
//    });

    void init(Screen var1, ButtonList var2);

    public interface ButtonList {
        <T extends GuiEventListener & Widget & NarratableEntry> T addButton(T var1);
    }
}
