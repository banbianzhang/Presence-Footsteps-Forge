package eu.ha3.presencefootsteps;

import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.minecraft.client.KeyMapping;
import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.annotation.Nullable;

@Mod(PresenceFootsteps.MOD_ID)
public class PresenceFootsteps {
    public static final Logger logger = LogManager.getLogger("PFSolver");

    public static final String MOD_ID = "presencefootsteps";

    private static PresenceFootsteps instance;

    public static PresenceFootsteps getInstance() {
        return instance;
    }

    private SoundEngine engine;

    private PFConfig config;

    private PFDebugHud debugHud;

    @Nullable
    public Lazy<KeyMapping> keyBinding = null;

    public PresenceFootsteps() {
        instance = this;
    }

    public PFDebugHud getDebugHud() {
        return debugHud;
    }

    public void setDebugHud(PFDebugHud debugHud) {
        this.debugHud = debugHud;
    }

    public SoundEngine getEngine() {
        return engine;
    }

    public void setEngine(SoundEngine engine) {
        this.engine = engine;
    }

    public PFConfig getConfig() {
        return config;
    }

    public void setConfig(PFConfig config) {
        this.config = config;
    }
}
