package me.drex.villagerconfig.common.platform;

import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public interface PlatformHelper {
    boolean isModLoaded(String modId);

    String getVersion();

    Path getConfigDir();

    void sendPacket(ServerPlayer player, CustomPacketPayload payload);

    default Path getModConfigDir() {
        return getConfigDir().resolve("VillagerConfig");
    }
}
