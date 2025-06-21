package me.drex.villagerconfig.fabric;

import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.commands.VillagerConfigCommand;
import me.drex.villagerconfig.fabric.util.FabricTradeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.object.builder.v1.trade.TradeOfferHelper;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.Items;

public class VillagerConfigFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        VillagerConfig.initialize();
        CommandRegistrationCallback.EVENT.register((dispatcher, commandBuildContext, environment) -> {
            VillagerConfigCommand.register(dispatcher, commandBuildContext);
        });

        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(FabricTradeManager.ID, provider -> {
            var tradeManager = new FabricTradeManager(provider);
            VillagerConfig.TRADE_MANAGER = tradeManager;
            return tradeManager;
        });
    }

}
