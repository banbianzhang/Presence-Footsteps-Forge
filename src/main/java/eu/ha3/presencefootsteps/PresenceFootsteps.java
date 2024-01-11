package eu.ha3.presencefootsteps;

import net.minecraftforge.common.util.Lazy;
import net.minecraftforge.fml.common.Mod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import eu.ha3.presencefootsteps.sound.SoundEngine;
import net.minecraft.client.KeyMapping;

@Mod(PresenceFootsteps.MOD_ID)
public class PresenceFootsteps {
    public static final Logger logger = LogManager.getLogger("PFSolver");

    public static final String MOD_ID = "presencefootsteps";

    private static PresenceFootsteps instance;

    public static PresenceFootsteps getInstance() {
        return instance;
    }

    public SoundEngine engine;

    public PFConfig config;

    public PFDebugHud debugHud;

    @Nullable
    public Lazy<KeyMapping> keyBinding = null;

    public PresenceFootsteps() {
        instance = this;
    }
}
