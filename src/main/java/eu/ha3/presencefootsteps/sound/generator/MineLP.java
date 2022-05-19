package eu.ha3.presencefootsteps.sound.generator;

//import com.minelittlepony.api.pony.meta.Race;
//import com.minelittlepony.client.MineLittlePony;

//import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public class MineLP {
    private static boolean checkCompleted = false;
    private static boolean hasMineLP;

    public static boolean hasPonies() {
        hasMineLP = false;
        checkCompleted = true;

        return hasMineLP;
    }

    public static Locomotion getLocomotion(Entity entity, Locomotion fallback) {

        ResourceLocation texture = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(entity).getTextureLocation(entity);

//        Race race = MineLittlePony.getInstance().getManager().getPony(texture).getRace(false);

//        if (race.isHuman()) {
            return fallback;
//        }

//        return race.hasWings() ? Locomotion.FLYING : Locomotion.QUADRUPED;
    }

    public static Locomotion getLocomotion(Player ply) {
        return Locomotion.BIPED;
//        return MineLittlePony.getInstance().getManager().getPony(ply).canFly() ? Locomotion.FLYING : Locomotion.QUADRUPED;
    }
}
