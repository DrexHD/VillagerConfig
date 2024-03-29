package me.drex.villagerconfig;

import me.drex.villagerconfig.commands.VillagerConfigCommand;
import me.drex.villagerconfig.config.ConfigManager;
import me.drex.villagerconfig.util.TradeManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.packs.PackType;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class VillagerConfig implements ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("VillagerConfig");
    public static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve("VillagerConfig");
    public static final String MOD_ID = "villagerconfig";
    public static final TradeManager TRADE_MANAGER = new TradeManager();

    @Override
    public void onInitialize() {
        ResourceManagerHelper.get(PackType.SERVER_DATA).registerReloadListener(TRADE_MANAGER);
        ConfigManager.load();
        CommandRegistrationCallback.EVENT.register((dispatcher, registryAccess, environment) -> {
            VillagerConfigCommand.register(dispatcher);
        });
    }

    public static String modId(String path) {
        return MOD_ID + ":" + path;
    }
}
