package eu.ha3.presencefootsteps.sound.generator;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Supplier;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;

public enum Locomotion {
    NONE(() -> StepSoundGenerator.EMPTY),
    BIPED(() -> new TerrestrialStepSoundGenerator(new Modifier<>())),
    QUADRUPED(() -> new TerrestrialStepSoundGenerator(new QuadrupedModifier())),
    FLYING(() -> new WingedStepSoundGenerator(new QuadrupedModifier())),
    FLYING_BIPED(() -> new WingedStepSoundGenerator(new Modifier<>()));

    private static final Map<String, Locomotion> registry = new HashMap<>();

    static {
        for (Locomotion i : values()) {
            registry.put(i.name(), i);
            registry.put(String.valueOf(i.ordinal()), i);
        }
    }

    private final Supplier<StepSoundGenerator> constructor;

    private static final String AUTO_TRANSLATION_KEY = "menu.pf.stance.auto";
    private final String translationKey = "menu.pf.stance." + name().toLowerCase();


    Locomotion(Supplier<StepSoundGenerator> gen) {
        constructor = gen;
    }

    public StepSoundGenerator supplyGenerator() {
        return constructor.get();
    }

    public Component getOptionName() {
        return new TranslatableComponent("menu.pf.stance", new TranslatableComponent(this == NONE ? AUTO_TRANSLATION_KEY : translationKey));
    }

    public String getDisplayName() {
        return I18n.get("pf.stance", I18n.get(translationKey));
    }

    public static Locomotion byName(String name) {
        return registry.getOrDefault(name, BIPED);
    }

    public static Locomotion forLiving(Entity entity, Locomotion fallback) {
        if (MineLP.hasPonies()) {
            return MineLP.getLocomotion(entity, fallback);
        }

        return fallback;
    }

    public static Locomotion forPlayer(Player ply, Locomotion preference) {
        if (preference == NONE) {
            if (ply instanceof LocalPlayer && MineLP.hasPonies()) {
                return MineLP.getLocomotion(ply);
            }

            return Locomotion.BIPED;
        }

        return preference;
    }
}
