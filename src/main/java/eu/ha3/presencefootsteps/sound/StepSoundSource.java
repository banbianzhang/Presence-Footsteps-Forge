package eu.ha3.presencefootsteps.sound;

import java.util.Optional;
import net.minecraft.world.entity.LivingEntity;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import eu.ha3.presencefootsteps.sound.generator.StepSoundGenerator;

public interface StepSoundSource {
    Optional<StepSoundGenerator> getStepGenerator(SoundEngine engine);

    final class Container implements StepSoundSource {
        private Locomotion locomotion;
        private Optional<StepSoundGenerator> stepSoundGenerator;

        private final LivingEntity entity;

        public Container(LivingEntity entity) {
            this.entity = entity;
        }

        @Override
        public Optional<StepSoundGenerator> getStepGenerator(SoundEngine engine) {
            Locomotion loco = engine.getLocomotion(entity);

            if (stepSoundGenerator == null || loco != locomotion) {
                locomotion = loco;
                stepSoundGenerator = loco.supplyGenerator();
            }
            return stepSoundGenerator;
        }
    }
}
