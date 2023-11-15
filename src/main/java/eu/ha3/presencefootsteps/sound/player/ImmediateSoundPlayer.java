package eu.ha3.presencefootsteps.sound.player;

import java.util.Random;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import eu.ha3.presencefootsteps.util.PlayerUtil;
import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.sound.SoundEngine;
import eu.ha3.presencefootsteps.world.Association;

/**
 * A Library that can also play sounds and default footsteps.
 *
 * @author Hurry
 */
public class ImmediateSoundPlayer implements SoundPlayer, StepSoundPlayer {
    private final Random random = new Random();

    private final DelayedSoundPlayer delayedPlayer = new DelayedSoundPlayer(this);

    private final SoundEngine engine;

    public ImmediateSoundPlayer(SoundEngine engine) {
        this.engine = engine;
    }

    @Override
    public Random getRNG() {
        return random;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void playStep(Association assos) {
        SoundType soundType = assos.getSoundGroup();

        if (!assos.getState().liquid() && soundType != null) {
            BlockState beside = assos.getSource().level().getBlockState(assos.getPos().above());

            if (beside.getBlock() == Blocks.SNOW) {
                soundType = Blocks.SNOW.getSoundType(beside);
            }

            playAttenuatedSound(assos.getSource(), soundType.getStepSound().getLocation().toString(), soundType.getVolume() * 0.15F, soundType.getPitch());
        }
    }

    @Override
    public void playSound(LivingEntity location, String soundName, float volume, float pitch, Options options) {

        if (options.containsKey("delay_min") && options.containsKey("delay_max")) {
            delayedPlayer.playSound(location, soundName, volume, pitch, options);

            return;
        }

        volume *= options.getOrDefault("volume_percentage", 1F);
        playAttenuatedSound(location, soundName, volume, pitch);
    }

    private void playAttenuatedSound(LivingEntity location, String soundName, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        double distance = mc.gameRenderer.getMainCamera().getPosition().distanceToSqr(location.position());

        volume *= engine.getVolumeForSource(location);
        pitch /= ((PlayerUtil.getScale(location) - 1) * 0.6F) + 1;

        SimpleSoundInstance sound = new UncappedSoundInstance(soundName, volume, pitch, location);

        if (distance > 100) {
            mc.getSoundManager().playDelayed(sound, (int) Math.floor(Math.sqrt(distance) / 2));
        } else {
            mc.getSoundManager().play(sound);
        }
    }

    @Override
    public void think() {
        delayedPlayer.think();
    }

    public static class UncappedSoundInstance extends SimpleSoundInstance {
        public UncappedSoundInstance(String soundName, float volume, float pitch, Entity entity) {
            super(getSoundId(soundName, entity),
                    entity.getSoundSource(),
                    volume, pitch, SoundInstance.createUnseededRandom(), false, 0,
                    SoundInstance.Attenuation.LINEAR,
                    entity.getX(),
                    entity.getY(),
                    entity.getZ(),
                    false);
        }

        public float getMaxVolume() {
            return 3;
        }

        private static ResourceLocation getSoundId(String name, Entity location) {
            if (name.indexOf(':') >= 0) {
                return new ResourceLocation(name);
            }

            String domain = "presencefootsteps";

            if (!PlayerUtil.isClientPlayer(location)) {
                domain += "mono"; // Switch to mono if playing another player
            }

            return new ResourceLocation(domain, name);
        }
    }
}
