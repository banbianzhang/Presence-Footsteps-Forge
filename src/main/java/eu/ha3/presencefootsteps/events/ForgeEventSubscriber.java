package eu.ha3.presencefootsteps.events;

import eu.ha3.presencefootsteps.PresenceFootsteps;
import net.minecraft.client.Minecraft;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.Optional;

@Mod.EventBusSubscriber(modid = "presencefootsteps", bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
public class ForgeEventSubscriber {

    @SubscribeEvent
    public static void onClientTick(final TickEvent.ClientTickEvent event) {
        Minecraft client = Minecraft.getInstance();
        Optional.ofNullable(client.getCameraEntity()).filter(e -> !e.isRemoved()).ifPresent(cameraEntity -> {
            PresenceFootsteps.getInstance().getEngine().onFrame(client, cameraEntity);
        });
    }
}
