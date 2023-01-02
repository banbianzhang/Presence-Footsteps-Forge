package eu.ha3.presencefootsteps.sound.generator;

import net.minecraft.world.entity.Entity;

public class MineLP {
    public static boolean hasPonies() {
        return false;
    }

    public static Locomotion getLocomotion(Entity entity, Locomotion fallback) {
        return fallback;
    }
}
