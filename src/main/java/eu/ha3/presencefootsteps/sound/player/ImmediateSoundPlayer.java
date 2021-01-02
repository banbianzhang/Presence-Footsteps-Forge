package eu.ha3.presencefootsteps.sound.player;

import eu.ha3.presencefootsteps.sound.Options;
import eu.ha3.presencefootsteps.util.PlayerUtil;
import eu.ha3.presencefootsteps.world.Association;
import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.block.SoundType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.ISound;
import net.minecraft.client.audio.SimpleSound;
import net.minecraft.entity.Entity;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.ResourceLocation;

import java.util.Random;

/**
 * A Library that can also play sounds and default footsteps.
 *
 * @author Hurry
 */
public class ImmediateSoundPlayer implements SoundPlayer, StepSoundPlayer {

    private final Random random = new Random();

    private final DelayedSoundPlayer delayedPlayer = new DelayedSoundPlayer(this);

    @Override
    public Random getRNG() {
        return random;
    }

    @Override
    public void playStep(Association assos) {
        SoundType soundType = assos.getSoundGroup();

        if (!assos.getMaterial().isLiquid() && soundType != null) {
            BlockState beside = assos.getSource().world.getBlockState(assos.getPos().up());

            if (beside.getBlock() == Blocks.SNOW) {
                soundType = Blocks.SNOW.getSoundType(beside);
            }

            playAttenuatedSound(assos.getSource(), soundType.getStepSound().getName().toString(), soundType.getVolume() * 0.15F, soundType.getPitch());
        }
    }

    @Override
    public void playSound(Entity location, String soundName, float volume, float pitch, Options options) {

        if (options.containsKey("delay_min") && options.containsKey("delay_max")) {
            delayedPlayer.playSound(location, soundName, volume, pitch, options);

            return;
        }

        playAttenuatedSound(location, soundName, volume, pitch);
    }

    private void playAttenuatedSound(Entity location, String soundName, float volume, float pitch) {
        Minecraft mc = Minecraft.getInstance();
        double distance = mc.gameRenderer.getActiveRenderInfo().getProjectedView().squareDistanceTo(location.getPositionVec());

        volume *= (100 - distance) / 100F;

        SimpleSound sound = createSound(getSoundId(soundName, location), volume, pitch, location);

        if (distance > 100) {
            mc.getSoundHandler().playDelayed(sound, (int) Math.floor(Math.sqrt(distance) / 2));
        } else {
            mc.getSoundHandler().play(sound);
        }
    }

    @Override
    public void think() {
        delayedPlayer.think();
    }

    private SimpleSound createSound(ResourceLocation id, float volume, float pitch, Entity entity) {
        return new SimpleSound(id,
                SoundCategory.MASTER,
                volume, pitch, false, 0,
                ISound.AttenuationType.LINEAR,
                (float) entity.getPosX(),
                (float) entity.getPosY(),
                (float) entity.getPosZ(),
                false);
    }

    private ResourceLocation getSoundId(String name, Entity location) {
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
