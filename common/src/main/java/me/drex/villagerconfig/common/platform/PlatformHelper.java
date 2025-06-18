package me.drex.villagerconfig.common.platform;

import java.nio.file.Path;

public interface PlatformHelper {
    boolean isModLoaded(String modId);

    String getVersion();

    Path getConfigDir();
}
