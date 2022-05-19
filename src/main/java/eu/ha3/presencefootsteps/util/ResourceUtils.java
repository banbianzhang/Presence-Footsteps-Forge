package eu.ha3.presencefootsteps.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import eu.ha3.presencefootsteps.PresenceFootsteps;

public interface ResourceUtils {

    static void forEach(ResourceLocation id, ResourceManager manager, Consumer<Reader> consumer) {
        try {
            manager.getResources(id).forEach(res -> {
                try (Reader stream = new InputStreamReader(res.getInputStream())) {
                    consumer.accept(stream);
                } catch (Exception e) {
                    PresenceFootsteps.logger.error("Error encountered loading resource " + res.getLocation() + " from pack" + res.getSourceName(), e);
                }
            });
        } catch (IOException e) {
            PresenceFootsteps.logger.error("Error encountered opening resources for " + id, e);
        }
    }

    static void forEachReverse(ResourceLocation id, ResourceManager manager, Consumer<Reader> consumer) {
        try {
            List<Resource> resources = manager.getResources(id);
            for (int i = resources.size() - 1; i >= 0; i--) {
                Resource res = resources.get(i);
                try (Reader stream = new InputStreamReader(res.getInputStream())) {
                    consumer.accept(stream);
                } catch (Exception e) {
                    PresenceFootsteps.logger.error("Error encountered loading resource " + res.getLocation() + " from pack" + res.getSourceName(), e);
                }
            }
        } catch (IOException e) {
            PresenceFootsteps.logger.error("Error encountered opening resources for " + id, e);
        }
    }
}
