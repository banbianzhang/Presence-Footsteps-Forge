package com.minelittlepony.common.client.gui;

import com.minelittlepony.common.client.gui.dimension.IBounded;
import com.mojang.blaze3d.platform.InputConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

public class GameGui extends Screen implements IViewRoot, IBounded, ITextContext, IViewRootDefaultImpl {
    protected final @Nullable Screen parent;

    protected GameGui(Component title) {
        this(title, Minecraft.getInstance().screen);
    }

    protected GameGui(Component title, @Nullable Screen parent) {
        super(title);
        this.parent = parent;
    }

    public static void playSound(SoundEvent event) {
        Minecraft.getInstance().getSoundManager().play(SimpleSoundInstance.forUI(event, 1.0F));
    }

    public static boolean isKeyDown(int key) {
        return InputConstants.isKeyDown(Minecraft.getInstance().getWindow().getWindow(), key);
    }

    public static Supplier<Boolean> keyCheck(int key) {
        return () -> {
            return isKeyDown(key);
        };
    }

    public void finish() {
        this.onClose();
        this.minecraft.setScreen(this.parent);
    }
}
