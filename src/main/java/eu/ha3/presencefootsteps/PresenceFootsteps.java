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
import net.fabricmc.fabric.api.resource.ResourcePackActivationType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.ChatFormatting;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.SystemToast;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;

public class PresenceFootsteps implements ClientModInitializer {
    public static final Logger logger = LogManager.getLogger("PFSolver");

    private static final String MODID = "presencefootsteps";
    private static final String KEY_BINDING_CATEGORY = "key.category." + MODID;
    private static final String UPDATER_ENDPOINT = "https://raw.githubusercontent.com/Sollace/Presence-Footsteps/master/version/latest.json";

    public static final Component MOD_NAME = Component.translatable("mod.presencefootsteps.name");
    private static final Component SOUND_PACK_NAME = Component.translatable("pf.default_sounds.name");

    private static PresenceFootsteps instance;

    public static PresenceFootsteps getInstance() {
        return instance;
    }

    private SoundEngine engine;

    private PFConfig config;

    private PFDebugHud debugHud;

    private UpdateChecker updater;

    private KeyMapping optionsKeyBinding;
    private KeyMapping toggleKeyBinding;
    private boolean toggleTriggered;

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

    public KeyMapping getOptionsKeyBinding() {
        return optionsKeyBinding;
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

        optionsKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.presencefootsteps.settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F10, KEY_BINDING_CATEGORY));
        toggleKeyBinding = KeyBindingHelper.registerKeyBinding(new KeyMapping("key.presencefootsteps.toggle", InputConstants.Type.KEYSYM, InputConstants.UNKNOWN.getValue(), KEY_BINDING_CATEGORY));

        engine = new SoundEngine(config);
        debugHud = new PFDebugHud(engine);

        ClientTickEvents.END_CLIENT_TICK.register(this::onTick);
        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(engine);

        FabricLoader.getInstance().getModContainer("presencefootsteps").ifPresent(container -> {
            ResourceManagerHelper.registerBuiltinResourcePack(new ResourceLocation("presencefootsteps", "default_sound_pack"), container, SOUND_PACK_NAME, ResourcePackActivationType.DEFAULT_ENABLED);
        });
    }

    private void onTick(Minecraft client) {
        Optional.ofNullable(client.player).filter(e -> !e.isRemoved()).ifPresent(cameraEntity -> {
            if (client.screen == null) {
                if (optionsKeyBinding.isDown()) {
                    client.setScreen(new PFOptionsScreen(client.screen));
                }
                if (toggleKeyBinding.isDown()) {
                    if (!toggleTriggered) {
                        toggleTriggered = true;
                        config.toggleDisabled();
                    }
                } else {
                    toggleTriggered = false;
                }
            }

            engine.onFrame(client, cameraEntity);

            if (!FabricLoader.getInstance().isModLoaded("modmenu")) {
                updater.attempt();
            }

            if (config.getEnabled() && !engine.hasData() && config.isFirstRun()) {
                config.setNotFirstRun();
                showSystemToast(
                    Component.translatable("key.presencefootsteps.settings"),
                    Component.translatable("pf.default_sounds.missing", SOUND_PACK_NAME)
                );
            }
        });
    }

    private void onUpdate(TargettedVersion newVersion, TargettedVersion currentVersion) {
        showSystemToast(
                Component.translatable("pf.update.title"),
                Component.translatable("pf.update.text", newVersion.version().getFriendlyString(), newVersion.minecraft().getFriendlyString())
        );
    }

    void onEnabledStateChange(boolean enabled) {
        engine.reload();
        showSystemToast(
                MOD_NAME,
                Component.translatable("key.presencefootsteps.toggle." + (enabled ? "enabled" : "disabled")).withStyle(enabled ? ChatFormatting.GREEN : ChatFormatting.GRAY)
        );
    }

    public void showSystemToast(Component title, Component body) {
        Minecraft client = Minecraft.getInstance();
        client.getToasts().addToast(SystemToast.multiline(client, SystemToast.SystemToastId.PACK_LOAD_FAILURE, title, body));
    }
}
