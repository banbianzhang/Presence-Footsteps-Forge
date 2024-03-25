package eu.ha3.presencefootsteps.world;

import java.io.IOException;
import java.util.Map;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SoundType;
import eu.ha3.presencefootsteps.util.JsonObjectWriter;

public class GolemLookup extends AbstractSubstrateLookup<EntityType<?>> {
    @Override
    public SoundsKey getAssociation(EntityType<?> key, String substrate) {
        return getSubstrateMap(getId(key), substrate).getOrDefault(EntityType.getKey(key), SoundsKey.UNASSIGNED);
    }

    @Override
    protected ResourceLocation getId(EntityType<?> key) {
        return EntityType.getKey(key);
    }

    @Override
    public void writeToReport(boolean full, JsonObjectWriter writer, Map<String, SoundType> groups) throws IOException {
        writer.each(BuiltInRegistries.ENTITY_TYPE, type -> {
            if (full || !contains(type)) {
                writer.object(EntityType.getKey(type).toString(), () -> {
                    writer.object("associations", () -> {
                        getSubstrates().forEach(substrate -> {
                            try {
                                SoundsKey association = getAssociation(type, substrate);
                                if (association.isResult()) {
                                    writer.field(substrate, association.raw());
                                }
                            } catch (IOException ignore) {}
                        });
                    });
                });
            }
        });
    }
}
