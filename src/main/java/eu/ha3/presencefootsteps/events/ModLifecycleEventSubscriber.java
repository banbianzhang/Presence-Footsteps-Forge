package eu.ha3.presencefootsteps.events;

import com.minelittlepony.common.util.GamePaths;
import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

import java.nio.file.Path;

@Mod.EventBusSubscriber(modid = "presencefootsteps", bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModLifecycleEventSubscriber {

    @SubscribeEvent
    public static void onInitializeClient(final FMLClientSetupEvent event) {
        PresenceFootsteps.logger.info("Presence Footsteps starting");
        Path pfFolder = GamePaths.getConfigDirectory().resolve("presencefootsteps");

//        updater = new UpdateChecker(new UpdaterConfig(pfFolder.resolve("updater.json")), MOD_ID, UPDATER_ENDPOINT, this::onUpdate);

        PresenceFootsteps.getInstance().setConfig(new PFConfig(pfFolder.resolve("userconfig.json"), PresenceFootsteps.getInstance()));
        PresenceFootsteps.getInstance().getConfig().load();

//        PresenceFootsteps.keyBinding = new KeyMapping("presencefootsteps.settings.key", InputConstants.Type.KEYSYM, GLFW.GLFW_KEY_F10, "key.categories.misc");

//        KeyBindingHelper.registerKeyBinding(keyBinding);

        PresenceFootsteps.getInstance().engine = new SoundEngine(PresenceFootsteps.getInstance().getConfig());
//        PresenceFootsteps.getInstance().debugHud = new PFDebugHud(PresenceFootsteps.getInstance().engine);

        PresenceFootsteps.getInstance().getEngine().reload();

//        ResourceManagerHelper.get(PackType.CLIENT_RESOURCES).registerReloadListener(engine);
    }

}
