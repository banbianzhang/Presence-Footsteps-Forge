package eu.ha3.presencefootsteps.sound;

import eu.ha3.presencefootsteps.PFConfig;
import eu.ha3.presencefootsteps.PresenceFootsteps;
import eu.ha3.presencefootsteps.mixins.IEntity;
import eu.ha3.presencefootsteps.sound.acoustics.AcousticsJsonParser;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import eu.ha3.presencefootsteps.sound.generator.StepSoundGenerator;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.FlyingEntity;
import net.minecraft.entity.passive.WaterMobEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.resources.IFutureReloadListener;
import net.minecraft.resources.IResourceManager;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.SoundEvents;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.profiler.IProfiler;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.Consumer;

import net.minecraft.resources.IFutureReloadListener.IStage;

public class SoundEngine implements IFutureReloadListener {

    private static final ResourceLocation blockmap = new ResourceLocation("presencefootsteps", "config/blockmap.json");
    private static final ResourceLocation golemmap = new ResourceLocation("presencefootsteps", "config/golemmap.json");
    private static final ResourceLocation locomotionmap = new ResourceLocation("presencefootsteps", "config/locomotionmap.json");
    private static final ResourceLocation primitivemap = new ResourceLocation("presencefootsteps", "config/primitivemap.json");
    private static final ResourceLocation acoustics = new ResourceLocation("presencefootsteps", "config/acoustics.json");
    private static final ResourceLocation variator = new ResourceLocation("presencefootsteps", "config/variator.json");

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
        return config.getEnabled() && (client.isIntegratedServerRunning() || config.getEnabledMP());
    }

    private List<? extends Entity> getTargets(PlayerEntity ply) {
        if (config.getEnabledGlobal()) {
            AxisAlignedBB box = new AxisAlignedBB(ply.getPosition()).grow(16);

            return ply.world.getEntitiesInAABBexcluding((Entity)null, box, e ->
                        e instanceof LivingEntity
                    && !(e instanceof WaterMobEntity)
                    && !(e instanceof FlyingEntity)
                    && !e.isPassenger());
        } else {
            return ply.world.getPlayers();
        }
    }

    public void onFrame(Minecraft client, PlayerEntity player) {
        if (!client.isGamePaused() && isRunning(client)) {
            getTargets(player).forEach(e -> {
                StepSoundGenerator generator = ((StepSoundSource) e).getStepGenerator(this);
                generator.setIsolator(isolator);
                if (generator.generateFootsteps((LivingEntity)e)) {
                    ((IEntity) e).setNextStepDistance(Integer.MAX_VALUE);
                }
            });

            isolator.think(); // Delayed sounds
        }
    }

    public boolean onSoundRecieved(SoundEvent event, SoundCategory category) {

        if (category != SoundCategory.PLAYERS || !isRunning(Minecraft.getInstance())) {
            return false;
        }

        if (event == SoundEvents.ENTITY_PLAYER_SWIM
         || event == SoundEvents.ENTITY_PLAYER_SPLASH
         || event == SoundEvents.ENTITY_PLAYER_BIG_FALL
         || event == SoundEvents.ENTITY_PLAYER_SMALL_FALL) {
            return true;
        }

        String[] name = event.getName().getPath().split("\\.");

        return name.length > 0
                && "block".contentEquals(name[0])
                && "step".contentEquals(name[name.length - 1]);
    }

    public Locomotion getLocomotion(LivingEntity entity) {
        if (entity instanceof PlayerEntity) {
            return Locomotion.forPlayer((PlayerEntity)entity, config.getLocomotion());
        }
        return isolator.getLocomotionMap().lookup(entity);
    }

    @Override
    public CompletableFuture<Void> reload(IStage sync, IResourceManager sender,
            IProfiler serverProfiler, IProfiler clientProfiler,
            Executor serverExecutor, Executor clientExecutor) {

        sync.getClass();
        return sync.markCompleteAwaitingOthers(null).thenRunAsync(() -> {
            clientProfiler.startTick();
            clientProfiler.startSection("Reloading PF Sounds");
            reloadEverything(sender);
            clientProfiler.endSection();
            clientProfiler.endTick();
        }, clientExecutor);
    }

    public void reloadEverything(IResourceManager manager) {
        isolator = new PFIsolator(this);

        collectResources(blockmap, manager, isolator.getBlockMap()::load);
        collectResources(golemmap, manager, isolator.getGolemMap()::load);
        collectResources(primitivemap, manager, isolator.getPrimitiveMap()::load);
        collectResources(locomotionmap, manager, isolator.getLocomotionMap()::load);
        collectResources(acoustics, manager, new AcousticsJsonParser(isolator.getAcoustics())::parse);
        collectResources(variator, manager, isolator.getVariator()::load);
    }

    private void collectResources(ResourceLocation id, IResourceManager manager, Consumer<Reader> consumer) {
        try {
            manager.getAllResources(id).forEach(res -> {
                try (Reader stream = new InputStreamReader(res.getInputStream())) {
                    consumer.accept(stream);
                } catch (Exception e) {
                    PresenceFootsteps.logger.error("Error encountered loading resource " + res.getLocation() + " from pack" + res.getPackName(), e);
                }
            });
        } catch (IOException e) {
            PresenceFootsteps.logger.error("Error encountered opening resources for " + id, e);
        }
    }

    public void shutdown() {
        isolator = new PFIsolator(this);

        PlayerEntity player = Minecraft.getInstance().player;

        if (player != null) {
            ((IEntity) player).setNextStepDistance(0);
        }
    }
}
