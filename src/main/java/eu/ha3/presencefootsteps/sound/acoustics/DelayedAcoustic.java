package eu.ha3.presencefootsteps.sound.acoustics;

import com.google.gson.JsonObject;

import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.sound.State;
import eu.ha3.presencefootsteps.sound.player.SoundPlayer;
import eu.ha3.presencefootsteps.util.Period;
import eu.ha3.presencefootsteps.util.Range;
import net.minecraft.world.entity.LivingEntity;

record DelayedAcoustic(
        String soundName,
        Range volume,
        Range pitch,
        Period delay
) implements Acoustic {
    public static DelayedAcoustic fromJson(JsonObject json, AcousticsJsonParser context) {
        return new DelayedAcoustic(
                context.getSoundName(json.get("name").getAsString()),
                context.getVolumeRange().read("vol", json, context),
                context.getPitchRange().read("pitch", json, context),
                Period.fromJson(json, "delay")
        );
    }

    @Override
    public void playSound(SoundPlayer player, LivingEntity location, State event, Options inputOptions) {
        VaryingAcoustic.playSound(soundName, volume, pitch, delay, player, location, inputOptions);
    }
}