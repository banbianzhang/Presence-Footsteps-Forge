package eu.ha3.presencefootsteps;

import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.world.Emitter;
import net.minecraft.block.BlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.math.BlockRayTraceResult;
import net.minecraft.util.math.RayTraceResult;

import java.util.List;
import java.util.Map;

public class PFDebugHud {

    private final SoundEngine engine;

    public PFDebugHud(SoundEngine engine) {
        this.engine = engine;
    }

    public void render(RayTraceResult blockHit, RayTraceResult fluidHit, List<String> list) {
        Minecraft client = Minecraft.getInstance();

        if (blockHit.getType() == RayTraceResult.Type.BLOCK) {
            BlockState state = client.world.getBlockState(((BlockRayTraceResult)blockHit).getPos());

            renderSoundList("PF Sounds",
                    engine.getIsolator().getBlockMap().getAssociations(state),
                    list);
        }

        if (client.pointedEntity != null) {
            renderSoundList("PF Golem Sounds",
                    engine.getIsolator().getGolemMap().getAssociations(client.pointedEntity.getType()),
                    list);
            list.add(engine.getIsolator().getLocomotionMap().lookup(client.pointedEntity).getDisplayName());
        }
    }

    private void renderSoundList(String title, Map<String, String> sounds, List<String> list) {
        list.add("");
        list.add(title);
        if (sounds.isEmpty()) {
            list.add(Emitter.UNASSIGNED);
        } else {
            sounds.forEach((key, value) -> {
                list.add((key.isEmpty() ? "default" : key) + ": " + value);
            });
        }
    }
}
