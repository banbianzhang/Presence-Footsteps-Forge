package eu.ha3.presencefootsteps.sound;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.stream.Stream;

import org.jetbrains.annotations.Nullable;

import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.mixins.IEntity;
import eu.ha3.presencefootsteps.sound.acoustics.AcousticsJsonParser;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import eu.ha3.presencefootsteps.sound.generator.StepSoundGenerator;
import eu.ha3.presencefootsteps.util.ResourceUtils;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.client.Minecraft;
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
import net.minecraft.world.entity.player.Player;

public class SoundEngine implements IdentifiableResourceReloadListener {
    private static final ResourceLocation BLOCK_MAP = new ResourceLocation("presencefootsteps", "config/blockmap.json");
    private static final ResourceLocation GOLEM_MAP = new ResourceLocation("presencefootsteps", "config/golemmap.json");
    private static final ResourceLocation LOCOMOTION_MAP = new ResourceLocation("presencefootsteps", "config/locomotionmap.json");
    private static final ResourceLocation PRIMITIVE_MAP = new ResourceLocation("presencefootsteps", "config/primitivemap.json");
    private static final ResourceLocation ACOUSTICS = new ResourceLocation("presencefootsteps", "config/acoustics.json");
    private static final ResourceLocation VARIATOR = new ResourceLocation("presencefootsteps", "config/variator.json");

    private static final ResourceLocation ID = new ResourceLocation("presencefootsteps", "sounds");

    private PFIsolator isolator = new PFIsolator(this);

    private final PFConfig config;

    public SoundEngine(PFConfig config) {
        this.config = config;
    }

    public float getGlobalVolume() {
        return config.getVolume() / 100F;
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
        return cameraEntity.level.getEntities((Entity) null, cameraEntity.getBoundingBox().inflate(16), e -> {
            return e instanceof LivingEntity
                    && !(e instanceof WaterAnimal)
                    && !(e instanceof FlyingMob)
                    && !e.isPassenger()
                    && !((LivingEntity)e).isSleeping()
                    && (!(e instanceof Player) || !((Player)e).isSpectator())
                    && e.distanceTo(cameraEntity) <= 16
                    && (config.getEnabledGlobal() || (e instanceof Player));
        }).stream();
    }

    public void onFrame(Minecraft client, Entity cameraEntity) {
        if (!client.isPaused() && isRunning(client)) {
            getTargets(cameraEntity).forEach(e -> {
                StepSoundGenerator generator = ((StepSoundSource) e).getStepGenerator(this);
                generator.setIsolator(isolator);
                if (generator.generateFootsteps((LivingEntity)e)) {
                    ((IEntity) e).setNextStepDistance(Integer.MAX_VALUE);
                }
            });

            isolator.getSoundPlayer().think(); // Delayed sounds
        }
    }

    public boolean onSoundRecieved(@Nullable SoundEvent event, SoundSource category) {

        if (event == null || category != SoundSource.PLAYERS || !isRunning(Minecraft.getInstance())) {
            return false;
        }

        if (event == SoundEvents.PLAYER_SWIM
         || event == SoundEvents.PLAYER_SPLASH
         || event == SoundEvents.PLAYER_BIG_FALL
         || event == SoundEvents.PLAYER_SMALL_FALL) {
            return true;
        }

        String[] name = event.getLocation().getPath().split("\\.");

        return name.length > 0
                && "block".contentEquals(name[0])
                && "step".contentEquals(name[name.length - 1]);
    }

    public Locomotion getLocomotion(LivingEntity entity) {
        if (entity instanceof Player) {
            return Locomotion.forPlayer((Player)entity, config.getLocomotion());
        }
        return isolator.getLocomotionMap().lookup(entity);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ID;
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
