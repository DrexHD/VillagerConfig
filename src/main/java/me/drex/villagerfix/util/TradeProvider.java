package me.drex.villagerfix.util;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.json.TradeGsons;
import net.minecraft.data.DataCache;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class TradeProvider implements DataProvider {

    private static final Logger LOGGER = VillagerFix.LOGGER;
    private static final Gson GSON = TradeGsons.getTradeGsonBuilder().create();
    private static final Map<String, Class<? extends TradeOffers.Factory>> FACTORYNAME_TO_CLASS = new HashMap<>();
    public static final Identifier WANDERING_TRADER_ID = new Identifier("wanderingtrader");

    private final DataGenerator root;

    public TradeProvider(DataGenerator root) {
        this.root = root;
    }

    @Override
    public void run(DataCache cache) {
        // Save all villager trades
        for (VillagerProfession villagerProfession : Registry.VILLAGER_PROFESSION) {
            this.saveMerchantTrades(cache, Registry.VILLAGER_PROFESSION.getId(villagerProfession), TradeOffers.PROFESSION_TO_LEVELED_TRADE.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>()));
        }
        // Save wandering trader trades
        this.saveMerchantTrades(cache, WANDERING_TRADER_ID, TradeOffers.WANDERING_TRADER_TRADES);
    }

    private void saveMerchantTrades(DataCache cache, Identifier merchantId, Int2ObjectMap<TradeOffers.Factory[]> trades) {
        Path path = getOutput(this.root.getOutput(), merchantId);
        JsonArray jsonElements;
        try {
            jsonElements = serializeData(trades);
        } catch (Exception e) {
            LOGGER.error("Couldn't serialize trade data {}", path, e);
            return;
        }
        try {
            DataProvider.writeToPath(GSON, cache, jsonElements, path);
        } catch (IOException e) {
            LOGGER.error("Couldn't save trade data {}", path, e);
        }
    }

    @Override
    public String getName() {
        return "Trades";
    }

    private static Path getOutput(Path rootOutput, Identifier merchantId) {
        return rootOutput.resolve("data/" + merchantId.getNamespace() + "/trades/" + merchantId.getPath() + ".json");
    }

    private String deobfuscateFactory(Class<? extends TradeOffers.Factory> clazz) {
        final String className = clazz.getName();
        final String deobfuscated = Deobfuscator.deobfuscate(className);
        boolean isObfuscated = !deobfuscated.equals(className);
        if (isObfuscated) {
            final String humanReadable = deobfuscated.replaceAll("(?:[\\w]+\\.)+[\\w]+[?:$|.]([\\w]+)", "$1");
            FACTORYNAME_TO_CLASS.put(humanReadable, clazz);
            return humanReadable;
        } else {
            FACTORYNAME_TO_CLASS.put(clazz.getSimpleName(), clazz);
            return clazz.getSimpleName();
        }
    }

    private JsonArray serializeData(Int2ObjectMap<TradeOffers.Factory[]> trades) {
        JsonArray levels = new JsonArray();
        trades.forEach((i, factories) -> {
            JsonArray level = new JsonArray();
            for (final TradeOffers.Factory factory : factories) {
                final String factoryName = deobfuscateFactory(factory.getClass());
                final JsonObject jsonObject = GSON.toJsonTree(factory).getAsJsonObject();
                jsonObject.addProperty("type", factoryName);
                level.add(jsonObject);
            }
            levels.add(level);
        });
        return levels;
    }

}
