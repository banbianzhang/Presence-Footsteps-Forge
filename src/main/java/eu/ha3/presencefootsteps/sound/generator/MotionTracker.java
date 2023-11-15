package eu.ha3.presencefootsteps.sound.generator;

import eu.ha3.presencefootsteps.sound.State;
import eu.ha3.presencefootsteps.util.PlayerUtil;
import net.minecraft.client.player.RemotePlayer;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class MotionTracker {
    private double lastX;
    private double lastY;
    private double lastZ;

    protected double motionX;
    protected double motionY;
    protected double motionZ;

    private final TerrestrialStepSoundGenerator generator;

    public MotionTracker(TerrestrialStepSoundGenerator generator) {
        this.generator = generator;
    }

    public double getMotionX() {
        return motionX;
    }

    public double getMotionY() {
        return motionY;
    }

    public double getMotionZ() {
        return motionZ;
    }

    public double getHorizontalSpeed() {
        return motionX * motionX + motionZ * motionZ;
    }

    public boolean isStationary() {
        return motionX == 0 && motionZ == 0;
    }

    /**
     * Fills in the blanks that aren't present on the client when playing on a
     * remote server.
     */
    public void simulateMotionData(LivingEntity ply) {
        if (PlayerUtil.isClientPlayer(ply)) {
            motionX = ply.getDeltaMovement().x;
            motionY = ply.getDeltaMovement().y;
            motionZ = ply.getDeltaMovement().z;
        } else {
            // Other players don't send their motion data so we have to make our own
            // approximations.
            motionX = (ply.getX() - lastX);
            lastX = ply.getX();
            motionY = (ply.getY() - lastY);

            if (ply.onGround()) {
                motionY += 0.0784000015258789d;
            }

            lastY = ply.getY();

            motionZ = (ply.getZ() - lastZ);
            lastZ = ply.getZ();
        }

        if (ply instanceof RemotePlayer) {
            if (ply.level().getGameTime() % 1 == 0) {

                if (motionX != 0 || motionZ != 0) {
                    ply.moveDist += Math.sqrt(Math.pow(motionX, 2) + Math.pow(motionY, 2) + Math.pow(motionZ, 2)) * 0.8;
                } else {
                    ply.moveDist += Math.sqrt(Math.pow(motionX, 2) + Math.pow(motionZ, 2)) * 0.8;
                }

                if (ply.onGround()) {
                    ply.fallDistance = 0;
                } else if (motionY < 0) {
                    ply.fallDistance -= motionY * 200;
                }
            }
        }
    }

    public State pickState(LivingEntity ply, State walk, State run) {
        if (!PlayerUtil.isClientPlayer(ply)) {
            // Other players don't send motion data, so have to decide some other way
            if (ply.isSprinting()) {
                return run;
            }
            return walk;
        }
        return getHorizontalSpeed() > generator.variator.SPEED_TO_RUN ? run : walk;
    }

    public float getSpeedScalingRatio(LivingEntity entity) {
        generator.variator.RUNNING_RAMPUP_BEGIN = 0.011F;
        generator.variator.RUNNING_RAMPUP_END = 0.022F;
        double relativeSpeed = getHorizontalSpeed() + (getMotionY() * getMotionY()) - generator.variator.RUNNING_RAMPUP_BEGIN;
        double maxSpeed = generator.variator.RUNNING_RAMPUP_END - generator.variator.RUNNING_RAMPUP_BEGIN;
        return (float)Mth.clamp(relativeSpeed / maxSpeed, 0, 1);
    }
}
