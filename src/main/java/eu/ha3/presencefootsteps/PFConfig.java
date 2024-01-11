package eu.ha3.presencefootsteps;

import java.nio.file.Path;
import net.minecraft.CrashReportCategory;
import net.minecraft.util.Mth;
import eu.ha3.presencefootsteps.config.EntitySelector;
import eu.ha3.presencefootsteps.config.JsonFile;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;

public class PFConfig extends JsonFile {

    private int volume = 70;

    private int clientPlayerVolume = 100;
    private int otherPlayerVolume = 100;
    private int hostileEntitiesVolume = 100;
    private int passiveEntitiesVolume = 100;
    private int runningVolumeIncrease = 0;
    private int wetSoundsVolume = 50;
    private int maxSteppingEntities = 50;

    private String stance = "UNKNOWN";

    private boolean multiplayer = true;

    private boolean global = true;

    private EntitySelector targetEntities = EntitySelector.ALL;

    private transient final PresenceFootsteps pf;

    public PFConfig(Path file, PresenceFootsteps pf) {
        super(file);
        this.pf = pf;
    }

    public boolean toggleMultiplayer() {
        multiplayer = !multiplayer;
        save();

        return multiplayer;
    }

    public EntitySelector cycleTargetSelector() {
        targetEntities = EntitySelector.VALUES[(targetEntities.ordinal() + 1) % EntitySelector.VALUES.length];

        save();

        return targetEntities;
    }

    public Locomotion setLocomotion(Locomotion loco) {

        if (loco != getLocomotion()) {
            stance = loco.name();
            save();

            pf.engine.reload();
        }

        return loco;
    }

    public Locomotion getLocomotion() {
        return Locomotion.byName(stance);
    }

    public EntitySelector getEntitySelector() {
        return targetEntities;
    }

    public boolean getEnabledGlobal() {
        return global && getEnabled();
    }

    public boolean getEnabledMP() {
        return multiplayer && getEnabled();
    }

    public int getMaxSteppingEntities() {
        return Math.max(1, maxSteppingEntities);
    }

    public boolean getEnabled() {
        return getGlobalVolume() > 0;
    }

    public int getGlobalVolume() {
        return Mth.clamp(volume, 0, 100);
    }

    public int getHostileEntitiesVolume() {
        return Mth.clamp(hostileEntitiesVolume, 0, 100);
    }

    public int getPassiveEntitiesVolume() {
        return Mth.clamp(passiveEntitiesVolume, 0, 100);
    }

    public int getClientPlayerVolume() {
        return Mth.clamp(clientPlayerVolume, 0, 100);
    }

    public int getOtherPlayerVolume() {
        return Mth.clamp(otherPlayerVolume, 0, 100);
    }

    public int getRunningVolumeIncrease() {
        return Mth.clamp(runningVolumeIncrease, -100, 100);
    }

    public int getWetSoundsVolume() {
        return Mth.clamp(wetSoundsVolume, 0, 100);
    }

    public float setGlobalVolume(float volume) {
        volume = volumeScaleToInt(volume);

        if (this.volume != volume) {
            boolean wasEnabled = getEnabled();

            this.volume = (int)volume;
            save();

            if (getEnabled() != wasEnabled) {
                pf.engine.reload();
            }
        }

        return getGlobalVolume();
    }

    public float setHostileEntitiesVolume(float volume) {
        hostileEntitiesVolume = volumeScaleToInt(volume);
        save();
        return getHostileEntitiesVolume();
    }

    public float setPassiveEntitiesVolume(float volume) {
        hostileEntitiesVolume = volumeScaleToInt(volume);
        save();
        return getPassiveEntitiesVolume();
    }

    public float setClientPlayerVolume(float volume) {
        clientPlayerVolume = volumeScaleToInt(volume);
        save();
        return getClientPlayerVolume();
    }

    public float setOtherPlayerVolume(float volume) {
        otherPlayerVolume = volumeScaleToInt(volume);
        save();
        return getOtherPlayerVolume();
    }

    public float setWetSoundsVolume(float volume) {
        wetSoundsVolume = volumeScaleToInt(volume);
        save();
        return getWetSoundsVolume();
    }

    public float setRunningVolumeIncrease(float volume) {
        runningVolumeIncrease = volume > 97 ? 100 : volume < -97 ? -100 : (int)volume;
        save();
        return getRunningVolumeIncrease();
    }

    public void populateCrashReport(CrashReportCategory section) {
        section.setDetail("PF Global Volume", volume);
        section.setDetail("PF User's Selected Stance", stance + " (" + getLocomotion() + ")");
        section.setDetail("Enabled Global", global);
        section.setDetail("Enabled Multiplayer", multiplayer);
    }

    private static int volumeScaleToInt(float volume) {
        return volume > 97 ? 100 : volume < 3 ? 0 : (int)volume;
    }
}
