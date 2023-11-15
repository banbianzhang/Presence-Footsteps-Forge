package eu.ha3.presencefootsteps.sound.acoustics;

import com.google.gson.JsonObject;
import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.sound.State;
import eu.ha3.presencefootsteps.sound.player.SoundPlayer;
import net.minecraft.world.entity.LivingEntity;

record ChanceAcoustic(
        Acoustic acoustic,
        float probability
) implements Acoustic {
    public static Acoustic fromJson(JsonObject json, AcousticsJsonParser context) {
        Acoustic acoustic = context.solveAcoustic(json.get("acoustic"));
        float probability = json.get("probability").getAsFloat();

        return new ChanceAcoustic(acoustic, probability);
    }

    @Override
    public void playSound(SoundPlayer player, LivingEntity location, State event, Options inputOptions) {
        final float rand = player.getRNG().nextFloat();

        if (rand * 100 <= probability) {
            acoustic.playSound(player, location, event, inputOptions);
        }
    }
}