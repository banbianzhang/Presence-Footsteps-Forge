package com.minelittlepony.common.util;

import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;

public class GamePaths {

    public static Path getGameDirectory() { return FMLPaths.GAMEDIR.get(); }

    public static Path getConfigDirectory() { return FMLPaths.CONFIGDIR.get(); }
}
