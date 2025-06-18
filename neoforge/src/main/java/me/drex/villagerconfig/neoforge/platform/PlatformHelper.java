package me.drex.villagerconfig.neoforge.platform;

import me.drex.villagerconfig.neoforge.VillagerConfigNeoForge;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLPaths;

import java.nio.file.Path;

public class PlatformHelper implements me.drex.villagerconfig.common.platform.PlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public String getVersion() {
        return VillagerConfigNeoForge.MOD_VERSION;
    }

    @Override
    public Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }
}
