package eu.ha3.presencefootsteps;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.minelittlepony.common.util.GamePaths;
import com.mojang.blaze3d.platform.InputConstants;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class PresenceFootsteps implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger("PFSolver");

    private static final String MODID = "presencefootsteps";
    private static final String UPDATER_ENDPOINT = "https://raw.githubusercontent.com/Sollace/Presence-Footsteps/master/version/latest.json";

    private static PresenceFootsteps instance;

    public static PresenceFootsteps getInstance() {
        return instance;
    }

    private SoundEngine engine;

    private PFConfig config;

    private PFDebugHud debugHud;

    private KeyMapping keyBinding;

    public PresenceFootsteps() {
        instance = this;
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

    @Override
    public void onInitializeClient() {
        Path pfFolder = GamePaths.getConfigDirectory().resolve("presencefootsteps");

        config = new PFConfig(pfFolder.resolve("userconfig.json"), this);
        config.load();

        keyBinding = new KeyMapping("key.presencefootsteps.settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F10, "key.categories.misc");

        KeyBindingHelper.registerKeyBinding(keyBinding);

        engine = new SoundEngine(config);
        debugHud = new PFDebugHud(engine);

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(engine);

        FabricLoader.getInstance().getModContainer("presencefootsteps").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation("presencefootsteps", "default_sound_pack"), container, Component.translatable("pf.default_sounds.name"), ResourcePackActivationType.DEFAULT_ENABLED);
        });
    }

    private void onTick(Minecraft client) {
        Optional.ofNullable(client.player).filter(e -> !e.isRemoved()).ifPresent(cameraEntity -> {
            if (keyBinding.isDown() && client.screen == null) {
                client.setScreen(new PFOptionsScreen(client.screen));
            }

            engine.onFrame(client, cameraEntity);
        });
    }
}
