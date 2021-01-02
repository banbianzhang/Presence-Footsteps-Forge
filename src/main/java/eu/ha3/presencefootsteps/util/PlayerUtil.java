package eu.ha3.presencefootsteps.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.player.RemoteClientPlayerEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;

public class PlayerUtil {
    public static boolean isClientPlayer(Entity entity) {
        PlayerEntity client = Minecraft.getInstance().player;
        return entity instanceof PlayerEntity
                && !(entity instanceof RemoteClientPlayerEntity)
                && client != null
                && (client == entity || client.getUniqueID().equals(entity.getUniqueID()));
    }
}
