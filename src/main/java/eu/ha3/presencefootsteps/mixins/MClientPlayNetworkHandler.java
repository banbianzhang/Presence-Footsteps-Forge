package eu.ha3.presencefootsteps.mixins;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundSoundPacket;

@Mixin(ClientPacketListener.class)
public abstract class MClientPlayNetworkHandler implements ClientGamePacketListener {

    @Inject(method = "onPlaySound(Lnet/minecraft/network/packet/s2c/play/PlaySoundS2CPacket;)V", at = @At("HEAD"), cancellable = true)
    public void onHandleSoundEffect(ClientboundSoundPacket packet, CallbackInfo info) {
        if (PresenceFootsteps.getInstance().getEngine().onSoundRecieved(packet.getSound(), packet.getSource())) {
            info.cancel();
        }
    }
}
