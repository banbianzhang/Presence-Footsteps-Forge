package eu.ha3.presencefootsteps.sound.acoustics;

import java.io.Reader;
import java.util.HashMap;
import java.util.Map;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.util.Range;

/**
 * A JSON parser that creates a Library of Acoustics.
 *
 * @author Hurry
 */
public class AcousticsJsonParser {
    private final int ENGINEVERSION = 1;

    private String soundRoot = "";

    private static final Map<String, AcousticFactory> factories = new HashMap<>();

    private static final JsonParser PARSER = new JsonParser();

    private final Range defaultVolume = new Range(1);
    private final Range defaultPitch = new Range(1);

    static {
        factories.put("basic", VaryingAcoustic::new);
        factories.put("simultaneous", SimultaneousAcoustic::new);
        factories.put("delayed", DelayedAcoustic::new);
        factories.put("probability", WeightedAcoustic::fromJson);
    }

    private final AcousticLibrary lib;

    public AcousticsJsonParser(AcousticLibrary lib) {
        this.lib = lib;
    }

    public void parse(Reader reader) {
        try {
            doParse(reader);
        } catch (JsonParseException e) {
            PresenceFootsteps.logger.error("Error whilst loading acoustics", e);
        }
    }

    private void doParse(Reader reader) throws JsonParseException {
        soundRoot = "";
        defaultVolume.on(1);
        defaultPitch.on(1);

        JsonObject json = PARSER.parse(reader).getAsJsonObject();

        if (!"library".equals(json.get("type").getAsString())) {
            throw new JsonParseException("Invalid type: \"library\"");
        }

        if (json.get("engineversion").getAsInt() != ENGINEVERSION) {
            throw new JsonParseException("Unrecognised Engine version: " + ENGINEVERSION + " expected, got "
                    + json.get("engineversion").getAsInt());
        }

        if (!json.has("contents")) {
            throw new JsonParseException("Empty contents");
        }

        if (json.has("soundroot")) {
            soundRoot = json.get("soundroot").getAsString();
        }

        if (json.has("defaults")) {
            JsonObject defaults = json.getAsJsonObject("defaults");
            if (defaults.has("vol_min")) {
                defaultVolume.min = getPercentage(defaults, "vol_min");
            }
            if (defaults.has("vol_max")) {
                defaultVolume.max = getPercentage(defaults, "vol_max");
            }
            if (defaults.has("pitch_min")) {
                defaultPitch.min = getPercentage(defaults, "pitch_min");
            }
            if (defaults.has("pitch_max")) {
                defaultPitch.max = getPercentage(defaults, "pitch_max");
            }
        }

        json.getAsJsonObject("contents").entrySet().forEach(element -> {
            lib.addAcoustic(element.getKey(), new EventSelectorAcoustics(element.getValue().getAsJsonObject(), this));
        });
    }

    public Acoustic solveAcoustic(JsonElement unsolved) throws JsonParseException {
        Acoustic ret = null;

        if (unsolved.isJsonObject()) {
            ret = solveAcousticsCompound(unsolved.getAsJsonObject());
        } else if (unsolved.isJsonArray()) {
            ret = new SimultaneousAcoustic(unsolved.getAsJsonArray(), this);
        } else if (unsolved.isJsonPrimitive() && unsolved.getAsJsonPrimitive().isString()) {
            ret = new VaryingAcoustic(unsolved.getAsString(), this);
        }

        if (ret == null) {
            throw new JsonParseException("Unresolved Json element: \r\n" + unsolved.toString());
        }

        return ret;
    }

    private Acoustic solveAcousticsCompound(JsonObject unsolved) throws JsonParseException {

        String type = unsolved.has("type") ? unsolved.get("type").getAsString() : "basic";

        if (!factories.containsKey(type)) {
            throw new JsonParseException("Invalid type for acoustic `" + type + "`");
        }

        return factories.get(type).create(unsolved, this);
    }

    public Range getVolumeRange() {
        return defaultVolume;
    }

    public Range getPitchRange() {
        return defaultPitch;
    }

    public String getSoundName(String soundName) {
        if (soundName.charAt(0) != '@') {
            return soundRoot + soundName;
        }

        return soundName.replace("@", "");
    }

    public float getPercentage(JsonObject object, String param) {
        return object.get(param).getAsFloat() / 100F;
    }

    public interface AcousticFactory {
        Acoustic create(JsonObject json, AcousticsJsonParser context);
    }
}
