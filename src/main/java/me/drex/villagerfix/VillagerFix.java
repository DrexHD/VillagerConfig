package me.drex.villagerfix;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.api.TradeFactoryStorage;
import me.drex.villagerfix.api.VillagerFixAPI;
import me.drex.villagerfix.config.Config;
import me.drex.villagerfix.villager.TradeOfferParser;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

public class VillagerFix {

    public static final Logger LOGGER = LogManager.getLogger();
    public static final Path DATA_PATH = FabricLoader.getInstance().getConfigDir().resolve("VillagerData");
    public static final TradeFactoryStorage data = new TradeFactoryStorage();

    public static void initializeData() {
        loadConfig();
        FabricLoader.getInstance().getEntrypointContainers("villagerfix", VillagerFixAPI.class).forEach(entrypoint -> {
            VillagerFixAPI api = entrypoint.getEntrypoint();
            api.onInitialize(data);
        });
        initializeVillagerData();
    }

    public static void reload() {
        Config.load();
        TradeOfferParser.cache.clear();
    }

    private static void initializeVillagerData() {
        DATA_PATH.toFile().mkdirs();
        for (Map.Entry<VillagerProfession, Int2ObjectMap<TradeOffers.Factory[]>> entry : TradeOffers.PROFESSION_TO_LEVELED_TRADE.entrySet()) {
            saveDataToFile(entry.getKey().toString(), entry.getValue());
        }
        saveDataToFile("wandering_trader", TradeOffers.WANDERING_TRADER_TRADES);
        saveDataToFile("nitwit", new Int2ObjectArrayMap<>());
    }

    private static void saveDataToFile(String fileName, Int2ObjectMap<TradeOffers.Factory[]> map) {
        JSONArray jsonArr = new JSONArray();
        for (int i = 1; i <= map.size(); i++) {
            TradeOffers.Factory[] tradeOffers = map.get(i);
            JSONArray jsonArray = new JSONArray();
            for (TradeOffers.Factory factory : tradeOffers) {
                JSONObject serialize = data.serialize(factory);
                if (serialize != null) {
                    jsonArray.put(serialize);
                } else {
                    JSONObject unknown = new JSONObject();
                    unknown.put("type", "unknown");
                    jsonArray.put(unknown);
                }
            }
            jsonArr.put(jsonArray);
        }
        try {
            Path path = DATA_PATH.resolve(fileName + ".json");
            if (!path.toFile().exists()) {
                VillagerFix.LOGGER.info("Saving trade data (" + fileName + ")");
                Files.write(path, jsonArr.toString(4).getBytes());
            }
        } catch (Exception e) {
            VillagerFix.LOGGER.error("Couldn't save " + fileName + ".json", e);
        }
    }

    public static void loadConfig() {
        if (!Config.isConfigLoaded) {
            Config.load();
        }
    }

}
