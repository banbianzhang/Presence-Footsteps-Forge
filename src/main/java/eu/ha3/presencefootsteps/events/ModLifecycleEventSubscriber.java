package eu.ha3.presencefootsteps.events;

import com.mojang.blaze3d.platform.InputConstants;
import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.PFDebugHud;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.loading.FMLPaths;
import org.lwjgl.glfw.GLFW;

import java.nio.file.Path;

import static eu.ha3.presencefootsteps.PresenceFootsteps.logger;

@Mod.EventBusSubscriber(modid = PresenceFootsteps.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModLifecycleEventSubscriber {
    private static final PresenceFootsteps presenceFootsteps = PresenceFootsteps.getInstance();

    @SubscribeEvent
    public static void onInitializeClient(final FMLClientSetupEvent event) {
        logger.info("Presence Footsteps starting");
        Path pfFolder = FMLPaths.CONFIGDIR.get().resolve("presencefootsteps");

        presenceFootsteps.setConfig(new PFConfig(pfFolder.resolve("userconfig.json"), presenceFootsteps));
        presenceFootsteps.getConfig().load();

        initKeyBinding();

        presenceFootsteps.engine = new SoundEngine(presenceFootsteps.getConfig());
        presenceFootsteps.engine.reload();
        presenceFootsteps.setDebugHud(new PFDebugHud(presenceFootsteps.engine));

        //todo ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(engine);
    }

    @SubscribeEvent
    public static void registerKeyBinding(RegisterKeyMappingsEvent event) {
        initKeyBinding();
        event.register(presenceFootsteps.keyBinding.get());
    }

    private static void initKeyBinding() {
        if (presenceFootsteps.keyBinding == null) {
            presenceFootsteps.keyBinding = Lazy.of(() -> new KeyMapping("key.presencefootsteps.settings", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F10, "key.categories.misc"));
        }
    }
}
