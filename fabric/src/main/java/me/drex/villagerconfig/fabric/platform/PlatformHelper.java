package me.drex.villagerconfig.fabric.platform;

import me.drex.villagerconfig.common.VillagerConfig;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.nio.file.Path;

public class PlatformHelper implements me.drex.villagerconfig.common.platform.PlatformHelper {
    @Override
    public boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    @Override
    public String getVersion() {
        return FabricLoader.getInstance().getModContainer(VillagerConfig.MOD_ID).get()
            .getMetadata().getVersion().getFriendlyString();
    }

    @Override
    public Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    @Override
    public void sendPacket(ServerPlayer player, CustomPacketPayload payload) {
        ServerPlayNetworking.send(player, payload);
    }
}
