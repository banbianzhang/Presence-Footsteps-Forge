package eu.ha3.presencefootsteps.sound.generator;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;

public enum Locomotion {
    NONE,
    BIPED(engine -> new TerrestrialStepSoundGenerator(engine, new Modifier<>())),
    QUADRUPED(engine -> new TerrestrialStepSoundGenerator(engine, new QuadrupedModifier())),
    FLYING(engine -> new WingedStepSoundGenerator(engine, new QuadrupedModifier())),
    FLYING_BIPED(engine -> new WingedStepSoundGenerator(engine, new Modifier<>()));

    private static final Map<String, Locomotion> registry = new Object2ObjectOpenHashMap<>();

    static {
        for (Locomotion i : values()) {
            registry.put(i.name(), i);
            registry.put(String.valueOf(i.ordinal()), i);
        }
    }

    private final Function<SoundEngine, Optional<StepSoundGenerator>> constructor;

    private static final String AUTO_TRANSLATION_KEY = "menu.pf.stance.auto";
    private final String translationKey = "menu.pf.stance." + name().toLowerCase();

    Locomotion() {
        constructor = engine -> Optional.empty();
    }

    Locomotion(Function<SoundEngine, StepSoundGenerator> gen) {
        constructor = engine -> Optional.of(gen.apply(engine));
    }

    public Optional<StepSoundGenerator> supplyGenerator(SoundEngine engine) {
        return constructor.apply(engine);
    }

    public Component getOptionName() {
        return Component.translatable("menu.pf.stance", Component.translatable(this == NONE ? AUTO_TRANSLATION_KEY : translationKey));
    }

    public Component getOptionTooltip() {
        return Component.translatable(translationKey + ".tooltip");
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
