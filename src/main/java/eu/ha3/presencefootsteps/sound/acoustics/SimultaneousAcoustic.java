package eu.ha3.presencefootsteps.sound.acoustics;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.sound.State;
import eu.ha3.presencefootsteps.sound.player.SoundPlayer;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import java.util.List;
import net.minecraft.world.entity.LivingEntity;

/**
 * An acoustic that plays multiple other acoustics all at the same time.
 *
 * @author Hurry
 */
record SimultaneousAcoustic(List<Acoustic> acoustics) implements Acoustic {

    public static SimultaneousAcoustic of(JsonArray sim, AcousticsJsonParser context) {
        List<Acoustic> acoustics = new ObjectArrayList<>(sim.size());

        for (JsonElement i : sim) {
            acoustics.add(context.solveAcoustic(i));
        }

        return new SimultaneousAcoustic(acoustics);
    }

    public static SimultaneousAcoustic fromJson(JsonObject json, AcousticsJsonParser context) {
        return of(json.getAsJsonArray("array"), context);
    }

    @Override
    public void playSound(SoundPlayer player, LivingEntity location, State event, Options inputOptions) {
        acoustics.forEach(acoustic -> acoustic.playSound(player, location, event, inputOptions));
    }
}