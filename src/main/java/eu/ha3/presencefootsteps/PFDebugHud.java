package eu.ha3.presencefootsteps;

import java.util.*;
import net.minecraft.client.Minecraft;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.world.Emitter;

public class PFDebugHud {

    private final SoundEngine engine;

    PFDebugHud(SoundEngine engine) {
        this.engine = engine;
    }

    public void render(HitResult blockHit, HitResult fluidHit, List<String> list) {
        Minecraft client = Minecraft.getInstance();

        if (blockHit.getType() == HitResult.Type.BLOCK) {
            BlockState state = client.level.getBlockState(((BlockHitResult)blockHit).getBlockPos());

            renderSoundList("Primitive: " + state.getSoundType().getStepSound().getLocation()
                    + "@" + String.format(Locale.ENGLISH, "%.2f_%.2f", state.getSoundType().volume, state.getSoundType().pitch),
                    engine.getIsolator().primitives().getAssociations(state.getSoundType()),
                    list);

            renderSoundList("PF Sounds",
                    engine.getIsolator().blocks().getAssociations(state),
                    list);

            SoundType sound = state.getSoundType();
            renderSoundList("PF Prims",
                    engine.getIsolator().primitives().getAssociations(sound),
                    list);
        }

        if (client.crosshairPickEntity != null) {
            renderSoundList("PF Golem Sounds",
                    engine.getIsolator().golems().getAssociations(client.crosshairPickEntity.getType()),
                    list);
            list.add(engine.getIsolator().locomotions().lookup(client.crosshairPickEntity).getDisplayName());
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
