package me.drex.villagerconfig.fabric;

import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.commands.VillagerConfigCommand;
import me.drex.villagerconfig.fabric.util.FabricTradeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;

public class VillagerConfigFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        VillagerConfig.initialize();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            VillagerConfigCommand.register(dispatcher);
        });

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(FabricTradeManager.ID, provider -> {
            var tradeManager = new FabricTradeManager(provider);
            VillagerConfig.TRADE_MANAGER = tradeManager;
            return tradeManager;
        });
    }

}
