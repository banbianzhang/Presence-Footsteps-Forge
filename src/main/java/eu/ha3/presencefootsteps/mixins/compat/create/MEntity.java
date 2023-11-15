package eu.ha3.presencefootsteps.mixins.compat.create;

import org.apache.logging.log4j.util.TriConsumer;
import org.spongepowered.asm.mixin.Dynamic;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import eu.ha3.presencefootsteps.compat.ContraptionCollidable;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.Vec3;

@Mixin(value = Entity.class, priority = 9999 /* Run us last */)
abstract class MEntity implements ContraptionCollidable {

    private int lastCollidedContraptionStateTick = -1;
    private BlockState lastCollidedContraptionState = Blocks.AIR.defaultBlockState();

    @Dynamic(
        value = "forCollission(center, consumer) - Private member injected by Create. See: https://github.com/Fabricators-of-Create/Create/blob/49cc17e3de33c965b1c409130abe436821f7410c/src/main/java/com/simibubi/create/foundation/mixin/client/EntityContraptionInteractionMixin.java#L81C21-L81C21"
    )
    @Shadow
    private void forCollision(Vec3 anchorPos, TriConsumer<Object, BlockState, BlockPos> action) {}

    @Override
    public BlockState getCollidedStateAt(BlockPos pos) {
        if (lastCollidedContraptionStateTick != ((Entity)(Object)this).tickCount) {
            lastCollidedContraptionStateTick = ((Entity)(Object)this).tickCount;
            forCollision(((Entity)(Object)this).position().add(0, -0.2, 0), (unused, state, p) -> {
                if (pos.equals(p)) {
                    lastCollidedContraptionState = state;
                }
            });
        }
        return lastCollidedContraptionState;
    }
}
