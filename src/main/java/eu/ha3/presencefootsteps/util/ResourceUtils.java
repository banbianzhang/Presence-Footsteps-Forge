package eu.ha3.presencefootsteps.util;

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
        manager.getResourceStack(id).forEach(res -> {
            try (Reader stream = new InputStreamReader(res.open())) {
                consumer.accept(stream);
            } catch (Exception e) {
                PresenceFootsteps.logger.error("Error encountered loading resource " + id + " from pack" + res.sourcePackId(), e);
            }
        });
    }

    static void forEachReverse(ResourceLocation id, ResourceManager manager, Consumer<Reader> consumer) {
        List<Resource> resources = manager.getResourceStack(id);
        for (int i = resources.size() - 1; i >= 0; i--) {
            Resource res = resources.get(i);
            try (Reader stream = new InputStreamReader(res.open())) {
                consumer.accept(stream);
            } catch (Exception e) {
                PresenceFootsteps.logger.error("Error encountered loading resource " + id + " from pack" + res.sourcePackId(), e);
            }
        }
    }
}
