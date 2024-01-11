package eu.ha3.presencefootsteps.world;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import java.io.IOException;
import java.util.Map;
import java.util.Set;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SoundType;
import eu.ha3.presencefootsteps.util.JsonObjectWriter;

public class GolemLookup implements Lookup<EntityType<?>> {
    private final Map<String, Map<ResourceLocation, String>> substrates = new Object2ObjectLinkedOpenHashMap<>();

    @Override
    public String getAssociation(EntityType<?> key, String substrate) {
        Map<ResourceLocation, String> primitives = substrates.get(substrate);

        if (primitives == null) {
            // Check for default
            primitives = substrates.get(EMPTY_SUBSTRATE);
        }

        if (primitives == null) {
            return Emitter.UNASSIGNED;
        }

        return primitives.getOrDefault(EntityType.getKey(key), Emitter.UNASSIGNED);
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
    public boolean contains(EntityType<?> key) {
        final ResourceLocation primitive = EntityType.getKey(key);

        for (Map<ResourceLocation, String> primitives : substrates.values()) {
            if (primitives.containsKey(primitive)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void writeToReport(boolean full, JsonObjectWriter writer, Map<String, SoundType> groups) throws IOException {
        writer.each(BuiltInRegistries.ENTITY_TYPE, type -> {
            if (full || !contains(type)) {
                writer.object(EntityType.getKey(type).toString(), () -> {
                    writer.object("associations", () -> {
                        getSubstrates().forEach(substrate -> {
                            try {
                                String association = getAssociation(type, substrate);
                                if (Emitter.isResult(association)) {
                                    writer.field(substrate, association);
                                }
                            } catch (IOException ignore) {}
                        });
                    });
                });
            }
        });
    }
}
