package eu.ha3.presencefootsteps.sound.acoustics;

import com.google.gson.JsonObject;

import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.util.Period;

class DelayedAcoustic extends VaryingAcoustic {

    private final Period delay = new Period(0);

    public DelayedAcoustic(JsonObject json, AcousticsJsonParser context) {
        super(json, context);

        if (json.has("delay")) {
            delay.set(json.get("delay").getAsLong());
        } else {
            delay.set(json.get("delay_min").getAsLong(), json.get("delay_max").getAsLong());
        }
    }

    @Override
    protected Options getOptions() {
        return delay;
    }
}
