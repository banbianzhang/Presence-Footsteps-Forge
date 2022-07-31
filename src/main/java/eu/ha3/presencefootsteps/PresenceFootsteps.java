package eu.ha3.presencefootsteps;

import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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

//    private UpdateChecker updater;

    public KeyMapping keyBinding;

    public PresenceFootsteps() {
        instance = this;
        MinecraftForge.EVENT_BUS.register(this);
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
