package eu.ha3.presencefootsteps;

import java.nio.file.Path;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.glfw.GLFW;

import com.minelittlepony.common.util.GamePaths;
import com.mojang.blaze3d.platform.InputConstants;
import eu.ha3.mc.quick.update.TargettedVersion;
import eu.ha3.mc.quick.update.UpdateChecker;
import eu.ha3.mc.quick.update.UpdaterConfig;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.client.gui.components.toasts.ToastComponent;
import net.minecraft.network.chat.TranslatableComponent;
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

    private UpdateChecker updater;

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

    public UpdateChecker getUpdateChecker() {
        return updater;
    }

    @Override
    public void onInitializeClient() {
        Path pfFolder = GamePaths.getConfigDirectory().resolve("presencefootsteps");

        updater = new UpdateChecker(new UpdaterConfig(pfFolder.resolve("updater.json")), MODID, UPDATER_ENDPOINT, this::onUpdate);

        config = new PFConfig(pfFolder.resolve("userconfig.json"), this);
        config.load();

        keyBinding = new KeyMapping("presencefootsteps.settings.key", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F10, "key.categories.misc");

        KeyBindingHelper.registerKeyBinding(keyBinding);

        engine = new SoundEngine(config);
        debugHud = new PFDebugHud(engine);

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(engine);
    }

    private void onTick(Minecraft client) {
        Optional.ofNullable(client.getCameraEntity()).filter(e -> !e.isRemoved()).ifPresent(cameraEntity -> {
            if (keyBinding.isDown() && client.screen == null) {
                client.setScreen(new PFOptionsScreen(client.screen));
            }

            engine.onFrame(client, cameraEntity);
            updater.attempt();
        });
    }

    private void onUpdate(TargettedVersion newVersion, TargettedVersion currentVersion) {
        ToastComponent manager = Minecraft.getInstance().getToasts();

        SystemToast.add(manager, SystemToast.SystemToastIds.TUTORIAL_HINT,
                new TranslatableComponent("pf.update.title"),
                new TranslatableComponent("pf.update.text", newVersion.version().getFriendlyString(), newVersion.minecraft().getFriendlyString()));
    }
}
