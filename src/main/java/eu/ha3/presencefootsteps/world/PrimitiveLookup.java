package eu.ha3.presencefootsteps.world;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.SoundType;
import eu.ha3.presencefootsteps.util.JsonObjectWriter;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;

public class PrimitiveLookup implements Lookup<SoundType> {
    private final Map<String, Map<ResourceLocation, String>> substrates = new Object2ObjectLinkedOpenHashMap<>();

    @Override
    public String getAssociation(SoundType sounds, String substrate) {
        final ResourceLocation id = sounds.getStepSound().getLocation();
        Map<ResourceLocation, String> primitives = substrates.get(substrate);

        if (primitives == null) {
            // Check for break sound
            primitives = substrates.get("break_" + id.getPath());
        }

        if (primitives == null) {
            // Check for default
            primitives = substrates.get(EMPTY_SUBSTRATE);
        }

        if (primitives == null) {
            return Emitter.UNASSIGNED;
        }

        return primitives.getOrDefault(id, Emitter.UNASSIGNED);
    }

    @Override
    public Set<String> getSubstrates() {
        return substrates.keySet();
    }

    @Override
    public void add(String key, String value) {
        final String[] split = key.trim().split("@");
        final String primitive = split[0];
        final String substrate = split.length > 1 ? split[1] : EMPTY_SUBSTRATE;

        substrates
            .computeIfAbsent(substrate, s -> new Object2ObjectLinkedOpenHashMap<>())
            .put(new ResourceLocation(primitive), value);
    }

    @Override
    public boolean contains(SoundType sounds) {
        final ResourceLocation primitive = sounds.getStepSound().getLocation();

        for (Map<ResourceLocation, String> primitives : substrates.values()) {
            if (primitives.containsKey(primitive)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeToReport(boolean full, JsonObjectWriter writer, Map<String, SoundType> groups) throws IOException {
        writer.each(groups.values(), group -> {
            String substrate = String.format(Locale.ENGLISH, "%.2f_%.2f", group.volume, group.pitch);
            if (full || !contains(group)) {
                writer.field(group.getStepSound().getLocation().toString() + "@" + substrate, getAssociation(group, substrate));
            }
        });
    }
}
