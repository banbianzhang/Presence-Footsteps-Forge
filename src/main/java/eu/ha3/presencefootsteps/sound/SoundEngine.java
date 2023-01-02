package eu.ha3.presencefootsteps.sound;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Predicate;
import java.util.stream.Stream;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.Nullable;

import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.mixins.IEntity;
import eu.ha3.presencefootsteps.sound.acoustics.AcousticsJsonParser;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import eu.ha3.presencefootsteps.util.PlayerUtil;
import eu.ha3.presencefootsteps.util.ResourceUtils;
import net.minecraft.CrashReport;
import net.minecraft.CrashReportCategory;
import net.minecraft.ReportedException;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.FlyingMob;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.animal.WaterAnimal;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;

public class SoundEngine implements PreparableReloadListener {
    private static final ResourceLocation BLOCK_MAP = new ResourceLocation(PresenceFootsteps.MOD_ID, "config/blockmap.json");
    private static final ResourceLocation GOLEM_MAP = new ResourceLocation(PresenceFootsteps.MOD_ID, "config/golemmap.json");
    private static final ResourceLocation LOCOMOTION_MAP = new ResourceLocation(PresenceFootsteps.MOD_ID, "config/locomotionmap.json");
    private static final ResourceLocation PRIMITIVE_MAP = new ResourceLocation(PresenceFootsteps.MOD_ID, "config/primitivemap.json");
    private static final ResourceLocation ACOUSTICS = new ResourceLocation(PresenceFootsteps.MOD_ID, "config/acoustics.json");
    private static final ResourceLocation VARIATOR = new ResourceLocation(PresenceFootsteps.MOD_ID, "config/variator.json");

    private static final ResourceLocation ID = new ResourceLocation(PresenceFootsteps.MOD_ID, "sounds");

    private PFIsolator isolator = new PFIsolator(this);

    private final PFConfig config;

    public SoundEngine(PFConfig config) {
        this.config = config;
    }

    public float getVolumeForSource(LivingEntity source) {
        float volume = config.getGlobalVolume() / 100F;

        if (source instanceof Player) {
            if (PlayerUtil.isClientPlayer(source)) {
                volume *= config.getClientPlayerVolume() / 100F;
            } else {
                volume *= config.getOtherPlayerVolume() / 100F;
            }
        }

        float runningProgress = ((StepSoundSource) source).getStepGenerator(this)
                .map(generator -> {
                    config.getRunningVolumeIncrease();
                    return generator.getMotionTracker().getSpeedScalingRatio(source);
                })
                .orElse(0F);

        // volume gets cut off at 1, so invert the function and lower it, so walking slower makes it quieter
        // and running causes it to ramp up to 1.
        float runningDecrease = Math.min((config.getRunningVolumeIncrease() / 100F) * (1F - runningProgress), 0.96F);
        return volume * (1F - runningDecrease);
    }

    public Isolator getIsolator() {
        return isolator;
    }

    public void reload() {
        if (config.getEnabled()) {
            reloadEverything(Minecraft.getInstance().getResourceManager());
        } else {
            shutdown();
        }
    }

    public boolean isRunning(Minecraft client) {
        return config.getEnabled() && (client.isLocalServer() || config.getEnabledMP());
    }

    private Stream<? extends Entity> getTargets(Entity cameraEntity) {
        return cameraEntity.level.getEntities((Entity) null, cameraEntity.getBoundingBox().inflate(16), (Predicate<? super Entity>) e -> {
            return e instanceof LivingEntity
                    && !(e instanceof WaterAnimal)
                    && !(e instanceof FlyingMob)
                    && !(e instanceof Shulker
                            || e instanceof ArmorStand
                            || e instanceof Boat
                            || e instanceof AbstractMinecart)
                    && !isolator.getGolemMap().contains(e.getType())
                    && !e.isPassenger()
                    && !((LivingEntity)e).isSleeping()
                    && (!(e instanceof Player) || !((Player)e).isSpectator())
                    && e.distanceTo(cameraEntity) <= 16
                    && config.getEntitySelector().test(e);
        }).stream();
    }

    public void onFrame(Minecraft client, Entity cameraEntity) {
        if (!client.isPaused() && isRunning(client)) {
            getTargets(cameraEntity).forEach(e -> {
                try {
                    ((StepSoundSource) e).getStepGenerator(this).ifPresent(generator -> {
                        generator.setIsolator(isolator);
                        if (generator.generateFootsteps((LivingEntity)e)) {
                            ((IEntity) e).setNextStepDistance(Integer.MAX_VALUE);
                        }
                    });
                } catch (Throwable t) {
                    CrashReport report = CrashReport.forThrowable(t, "Generating PF sounds for entity");
                    CrashReportCategory section = report.addCategory("Entity being ticked");
                    if (e == null) {
                        section.setDetail("Entity Type", "null");
                    } else {
                        e.fillCrashReportCategory(section);
                        section.setDetail("Entity's Locomotion Type", isolator.getLocomotionMap().lookup(e));
                        section.setDetail("Entity is Golem", isolator.getGolemMap().contains(e.getType()));
                    }
                    config.populateCrashReport(report.addCategory("PF Configuration"));
                    throw new ReportedException(report);
                }
            });

            isolator.getSoundPlayer().think(); // Delayed sounds
        }
    }

    public boolean onSoundRecieved(@Nullable Holder<SoundEvent> event, SoundSource category) {
        if (event == null || category != SoundSource.PLAYERS || !isRunning(Minecraft.getInstance())) {
            return false;
        }

        return event.unwrap().right().filter(sound -> {
            if (event == SoundEvents.PLAYER_SWIM
                    || event == SoundEvents.PLAYER_SPLASH
                    || event == SoundEvents.PLAYER_BIG_FALL
                    || event == SoundEvents.PLAYER_SMALL_FALL) {
                       return true;
                   }

                   String[] name = sound.getLocation().getPath().split("\\.");
                   return name.length > 0
                           && "block".contentEquals(name[0])
                           && "step".contentEquals(name[name.length - 1]);
        }).isPresent();
    }

    public Locomotion getLocomotion(LivingEntity entity) {
        if (entity instanceof Player) {
            return Locomotion.forPlayer((Player)entity, config.getLocomotion());
        }
        return isolator.getLocomotionMap().lookup(entity);
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier sync, ResourceManager sender,
            ProfilerFiller serverProfiler, ProfilerFiller clientProfiler,
            Executor serverExecutor, Executor clientExecutor) {
        return sync.wait(null).thenRunAsync(() -> {
            clientProfiler.startTick();
            clientProfiler.push("Reloading PF Sounds");
            reloadEverything(sender);
            clientProfiler.pop();
            clientProfiler.endTick();
        }, clientExecutor);
    }

    public void reloadEverything(ResourceManager manager) {
        isolator = new PFIsolator(this);

        ResourceUtils.forEach(BLOCK_MAP, manager, isolator.getBlockMap()::load);
        ResourceUtils.forEach(GOLEM_MAP, manager, isolator.getGolemMap()::load);
        ResourceUtils.forEach(PRIMITIVE_MAP, manager, isolator.getPrimitiveMap()::load);
        ResourceUtils.forEach(LOCOMOTION_MAP, manager, isolator.getLocomotionMap()::load);
        ResourceUtils.forEach(ACOUSTICS, manager, new AcousticsJsonParser(isolator.getAcoustics())::parse);
        ResourceUtils.forEach(VARIATOR, manager, isolator.getVariator()::load);
    }

    public void shutdown() {
        isolator = new PFIsolator(this);

        Player player = Minecraft.getInstance().player;

        if (player != null) {
            ((IEntity) player).setNextStepDistance(0);
        }
    }
}
