package eu.ha3.presencefootsteps.mixins;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.client.gui.AbstractGui;
import net.minecraft.client.gui.overlay.DebugOverlayGui;
import net.minecraft.util.math.RayTraceResult;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(DebugOverlayGui.class)
public abstract class MDebugHud extends AbstractGui {

    @Shadow
    protected RayTraceResult rayTraceBlock;

    @Shadow
    protected RayTraceResult rayTraceFluid;

    @Inject(method = "getDebugInfoRight", at = @At("RETURN"))
    protected void onGetRightText(CallbackInfoReturnable<List<String>> info) {
        PresenceFootsteps.getInstance().getDebugHud().render(rayTraceBlock, rayTraceFluid, info.getReturnValue());
    }
}
