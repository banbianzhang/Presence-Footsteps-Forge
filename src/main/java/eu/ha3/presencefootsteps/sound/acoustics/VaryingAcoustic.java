package eu.ha3.presencefootsteps.sound.acoustics;

import com.google.gson.JsonObject;
import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.sound.State;
import eu.ha3.presencefootsteps.sound.player.SoundPlayer;
import eu.ha3.presencefootsteps.util.Range;
import net.minecraft.world.entity.LivingEntity;

/**
 * The simplest form of an acoustic. Plays one sound with a set volume and pitch range.
 *
 * @author Hurry
 */
record VaryingAcoustic(
        String soundName,
        Range volume,
        Range pitch
) implements Acoustic {
    public static VaryingAcoustic of(String name, AcousticsJsonParser context) {
        return new VaryingAcoustic(
                context.getSoundName(name),
                context.getVolumeRange(),
                context.getPitchRange()
        );
    }

    public static VaryingAcoustic fromJson(JsonObject json, AcousticsJsonParser context) {
        return new VaryingAcoustic(
                context.getSoundName(json.get("name").getAsString()),
                context.getVolumeRange().read("vol", json, context),
                context.getPitchRange().read("pitch", json, context)
        );
    }

    @Override
    public void playSound(SoundPlayer player, LivingEntity location, State event, Options inputOptions) {
        playSound(soundName, volume, pitch, Options.EMPTY, player, location, inputOptions);
    }

    // shared code between VaryingAcoustic & DelayedAcoustic since
    // in the old implementation DelayedAcoustic extended VaryingAcoustic
    static void playSound(String soundName, Range volume, Range pitch, Options options, SoundPlayer player, LivingEntity location, Options inputOptions) {
        if (soundName.isEmpty()) {
            // Special case for intentionally empty sounds (as opposed to fall back sounds)
            return;
        }

        final float finalVolume = inputOptions.containsKey("gliding_volume")
                ? volume.on(inputOptions.get("gliding_volume"))
                : volume.random(player.getRNG());

        final float finalPitch = inputOptions.containsKey("gliding_pitch")
                ? pitch.on(inputOptions.get("gliding_pitch"))
                : pitch.random(player.getRNG());

        player.playSound(location, soundName, finalVolume, finalPitch, options.and(inputOptions));
    }
}