package eu.ha3.presencefootsteps;

import java.nio.file.Path;
import java.util.Optional;

import com.minelittlepony.common.util.GamePaths;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.mojang.blaze3d.platform.InputConstants;
//import eu.ha3.mc.quick.update.TargettedVersion;
//import eu.ha3.mc.quick.update.UpdateChecker;
//import eu.ha3.mc.quick.update.UpdaterConfig;
import eu.ha3.presencefootsteps.sound.SoundEngine;
//import net.fabricmc.api.ClientModInitializer;
//import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
////import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
//import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.server.packs.PackType;

@Mod(PresenceFootsteps.MOD_ID)
public class PresenceFootsteps {
    public static final Logger logger = LogManager.getLogger("PFSolver");

    static final String MOD_ID = "presencefootsteps";
//    private static final String UPDATER_ENDPOINT = "https://raw.githubusercontent.com/Sollace/Presence-Footsteps/master/version/latest.json";

    private static PresenceFootsteps instance;

    public static PresenceFootsteps getInstance() {
        return instance;
    }

    public SoundEngine engine;

    private PFConfig config;

    public PFDebugHud debugHud;

//    private UpdateChecker updater;

    public KeyMapping keyBinding;

    public PresenceFootsteps() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
    }

    public PFDebugHud getDebugHud() {
        return debugHud;
    }

    public SoundEngine getEngine() {
        return engine;
    }

    public PFConfig getConfig() {
        return config;
    }

    public void setConfig(PFConfig config) {
        this.config = config;
    }

//    public UpdateChecker getUpdateChecker() {
//        return updater;
//    }
}
