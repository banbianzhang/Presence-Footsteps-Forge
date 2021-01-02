package eu.ha3.presencefootsteps.mixins;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.client.network.play.ClientPlayNetHandler;
import net.minecraft.client.network.play.IClientPlayNetHandler;
import net.minecraft.network.play.server.SPlaySoundEffectPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientPlayNetHandler.class)
public abstract class MClientPlayNetworkHandler implements IClientPlayNetHandler {

    @Inject(method = "handleSoundEffect(Lnet/minecraft/network/play/server/SPlaySoundEffectPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onHandleSoundEffect(SPlaySoundEffectPacket packet, CallbackInfo info) {
        if (PresenceFootsteps.getInstance().getEngine().onSoundRecieved(packet.getSound(), packet.getCategory())) {
            info.cancel();
        }
    }
}
