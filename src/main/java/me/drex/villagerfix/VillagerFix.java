package me.drex.villagerfix;

import me.drex.villagerfix.commands.VillagerFixCommand;
import me.drex.villagerfix.config.Config;
import me.drex.villagerfix.factory.VF_EnchantBookFactory;
import me.drex.villagerfix.factory.VF_LootTableFactory;
import me.drex.villagerfix.factory.VF_TradeItemFactory;
import me.drex.villagerfix.json.JsonFactory;
import me.drex.villagerfix.util.Deobfuscator;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.server.MinecraftServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.nio.file.Path;

public class VillagerFix implements DedicatedServerModInitializer, ClientModInitializer, ModInitializer {

    public static final Logger LOGGER = LogManager.getLogger("VillagerFix");
    public static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve("VillagerFix");
    private static boolean init = false;
    private static MinecraftServer minecraftServer;

    public static JsonFactory getJsonFactory() {
        return jsonFactory;
    }

    private static final JsonFactory jsonFactory = new JsonFactory();

    public static void onStarted(MinecraftServer server) {
        if (init) return;
        minecraftServer = server;
        Deobfuscator.init();
        jsonFactory.saveTradeData();
        jsonFactory.loadTrades();
        jsonFactory.addCustomTradeFactories(VF_EnchantBookFactory.class, VF_TradeItemFactory.class, VF_LootTableFactory.class);
        init = true;
    }

    public static void reload() {
        Config.load();
        jsonFactory.loadTrades();
    }

    public static void loadConfig() {
        if (!Config.loaded) {
            Config.load();
        }
    }

    public static MinecraftServer getMinecraftServer() {
        return minecraftServer;
    }

    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(world -> VillagerFix.onStarted(world.getServer()));
    }

    @Override
    public void onInitializeServer() {
        ServerLifecycleEvents.SERVER_STARTING.register(VillagerFix::onStarted);
    }

    @Override
    public void onInitialize() {
        loadConfig();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            VillagerFixCommand.register(dispatcher);
        });
    }
}
