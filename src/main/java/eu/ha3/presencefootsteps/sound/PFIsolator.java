package eu.ha3.presencefootsteps.sound;

import eu.ha3.presencefootsteps.config.Variator;
import eu.ha3.presencefootsteps.sound.player.StepSoundPlayer;
import eu.ha3.presencefootsteps.sound.acoustics.AcousticLibrary;
import eu.ha3.presencefootsteps.sound.acoustics.AcousticsPlayer;
import eu.ha3.presencefootsteps.sound.generator.Locomotion;
import eu.ha3.presencefootsteps.sound.player.ImmediateSoundPlayer;
import eu.ha3.presencefootsteps.sound.player.SoundPlayer;
import eu.ha3.presencefootsteps.world.GolemLookup;
import eu.ha3.presencefootsteps.world.Index;
import eu.ha3.presencefootsteps.world.LocomotionLookup;
import eu.ha3.presencefootsteps.world.Lookup;
import eu.ha3.presencefootsteps.world.PFSolver;
import eu.ha3.presencefootsteps.world.PrimitiveLookup;
import eu.ha3.presencefootsteps.world.Solver;
import eu.ha3.presencefootsteps.world.StateLookup;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;

public class PFIsolator implements Isolator {
    private final Variator variator = new Variator();

    private final Index<Entity, Locomotion> locomotionMap = new LocomotionLookup();

    private final Lookup<EntityType<?>> golemMap = new GolemLookup();

    private final Lookup<BlockState> blockMap = new StateLookup();

    private final Lookup<SoundType> primitiveMap = new PrimitiveLookup();

    private final ImmediateSoundPlayer player;
    private final AcousticsPlayer acoustics;

    private final Solver solver = new PFSolver(this);

    public PFIsolator(SoundEngine engine) {
        this.player = new ImmediateSoundPlayer(engine);
        this.acoustics = new AcousticsPlayer(player);
    }

    @Override
    public AcousticLibrary getAcoustics() {
        return acoustics;
    }

    @Override
    public Solver getSolver() {
        return solver;
    }

    @Override
    public Index<Entity, Locomotion> getLocomotionMap() {
        return locomotionMap;
    }

    @Override
    public Lookup<EntityType<?>> getGolemMap() {
        return golemMap;
    }

    @Override
    public Lookup<BlockState> getBlockMap() {
        return blockMap;
    }

    @Override
    public Lookup<SoundType> getPrimitiveMap() {
        return primitiveMap;
    }

    @Override
    public SoundPlayer getSoundPlayer() {
        return player;
    }

    @Override
    public StepSoundPlayer getStepPlayer() {
        return player;
    }

    @Override
    public Variator getVariator() {
        return variator;
    }
}
