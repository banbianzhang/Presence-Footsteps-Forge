package eu.ha3.presencefootsteps.sound;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.mixins.IEntity;
import eu.ha3.presencefootsteps.util.PlayerUtil;
import eu.ha3.presencefootsteps.world.Solver;
import eu.ha3.presencefootsteps.world.PFSolver;
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
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Shulker;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.entity.vehicle.Boat;

public class SoundEngine implements PreparableReloadListener {
    //private static final ResourceLocation ID = new ResourceLocation("presencefootsteps", "sounds");

    private Isolator isolator = new Isolator(this);
    private final Solver solver = new PFSolver(this);

    private final PFConfig config;

    private boolean hasConfigurations;

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
        } else if (source instanceof Monster) {
            volume *= config.getHostileEntitiesVolume() / 100F;
        } else {
            volume *= config.getPassiveEntitiesVolume() / 100F;
        }

        float runningProgress = ((StepSoundSource) source).getStepGenerator(this)
                .map(generator -> generator.getMotionTracker().getSpeedScalingRatio(source))
                .orElse(0F);

        return volume * (1F + ((config.getRunningVolumeIncrease() / 100F) * runningProgress));
    }

    public Isolator getIsolator() {
        return isolator;
    }

    public Solver getSolver() {
        return solver;
    }

    public PFConfig getConfig() {
        return config;
    }

    public void reload() {
        if (config.getEnabled()) {
            reloadEverything(Minecraft.getInstance().getResourceManager());
        } else {
            shutdown();
        }
    }

    public boolean isRunning(Minecraft client) {
        return hasConfigurations && config.getEnabled() && (client.isLocalServer() || config.getEnabledMP());
    }

    private Stream<? extends Entity> getTargets(final Entity cameraEntity) {
        final List<? extends Entity> entities = cameraEntity.level().getEntities((Entity) null, cameraEntity.getBoundingBox().inflate(16), e -> {
            return e instanceof LivingEntity
                    && !(e instanceof WaterAnimal)
                    && !(e instanceof FlyingMob)
                    && !(e instanceof Shulker
                            || e instanceof ArmorStand
                            || e instanceof Boat
                            || e instanceof AbstractMinecart)
                        && !isolator.golems().contains(e.getType())
                        && !e.isPassenger()
                        && !((LivingEntity)e).isSleeping()
                        && (!(e instanceof Player) || !e.isSpectator())
                        && e.distanceToSqr(cameraEntity) <= 256
                        && config.getEntitySelector().test(e);
        });

        final Comparator<Entity> nearest = Comparator.comparingDouble(e -> e.distanceToSqr(cameraEntity));

        if (entities.size() < config.getMaxSteppingEntities()) {
            return entities.stream();
        }
        Set<Integer> alreadyVisited = new HashSet<>();
        return entities.stream()
            .sorted(nearest)
                    // Always play sounds for players and the entities closest to the camera
                        // If multiple entities share the same block, only play sounds for one of each distinct type
            .filter(e -> e == cameraEntity || e instanceof Player || (alreadyVisited.size() < config.getMaxSteppingEntities() && alreadyVisited.add(Objects.hash(e.getType(), e.blockPosition()))));
    }

    public void onFrame(Minecraft client, Entity cameraEntity) {
        if (!client.isPaused() && isRunning(client)) {
            getTargets(cameraEntity).forEach(e -> {
                try {
                    ((StepSoundSource) e).getStepGenerator(this).ifPresent(generator -> {
                        if (generator.generateFootsteps((LivingEntity)e)) {
                            ((IEntity) e).setNextStepDistance(Integer.MAX_VALUE);
                        } else if (((IEntity) e).getNextStepDistance() == Integer.MAX_VALUE) {
                            ((IEntity) e).setNextStepDistance(e.moveDist + 1);
                        }
                    });
                } catch (Throwable t) {
                    CrashReport report = CrashReport.forThrowable(t, "Generating PF sounds for entity");
                    CrashReportCategory section = report.addCategory("Entity being ticked");
                    if (e == null) {
                        section.setDetail("Entity Type", "null");
                    } else {
                        e.fillCrashReportCategory(section);
                        section.setDetail("Entity's Locomotion Type", isolator.locomotions().lookup(e));
                        section.setDetail("Entity is Golem", isolator.golems().contains(e.getType()));
                    }
                    config.populateCrashReport(report.addCategory("PF Configuration"));
                    throw new ReportedException(report);
                }
            });

            isolator.soundPlayer().think(); // Delayed sounds
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

                   //String[] name = sound.getId().getPath().split("\\.");
                   return false;//name.length > 0
                          // && "block".contentEquals(name[0])
                          // && "step".contentEquals(name[name.length - 1]);
        }).isPresent();
    }

    @Override
    public @NotNull CompletableFuture<Void> reload(PreparationBarrier sync, ResourceManager sender,
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
        isolator = new Isolator(this);
        hasConfigurations = isolator.load(manager);
    }

    public void shutdown() {
        isolator = new Isolator(this);
        hasConfigurations = false;

        Player player = Minecraft.getInstance().player;

        if (player != null) {
            ((IEntity) player).setNextStepDistance(0);
        }
    }
}