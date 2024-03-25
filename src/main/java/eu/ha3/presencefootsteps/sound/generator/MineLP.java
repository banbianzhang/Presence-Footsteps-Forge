package eu.ha3.presencefootsteps.sound.generator;

import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class MineLP {
    public static boolean hasPonies() {
        return false;
    }

    public static Locomotion getLocomotion(Entity entity, Locomotion fallback) {
        return fallback;
    }

    public static Locomotion getLocomotion(Player ply) {
        return Locomotion.BIPED;
    }
}
