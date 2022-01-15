package me.drex.villagerfix.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.factory.VF_EnchantBookFactory;
import me.drex.villagerfix.factory.VF_LootTableFactory;
import me.drex.villagerfix.factory.VF_TradeItemFactory;
import me.drex.villagerfix.json.TradeGsons;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.village.TradeOffers;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class TradeManager extends JsonDataLoader {

    private static final Logger LOGGER = VillagerFix.LOGGER;
    private static final Gson GSON = TradeGsons.getTradeGsonBuilder().create();
    private Map<Identifier, Int2ObjectMap<TradeOffers.Factory[]>> trades = ImmutableMap.of();
    private static final String REGEX = "(?:[\\w]+\\.)+[\\w]+[?:$|.]([\\w]+)";
    public static final Map<String, Class<? extends TradeOffers.Factory>> FACTORYNAME_TO_CLASS = new HashMap<>();

    public TradeManager() {
        super(GSON, "trades");
    }

    @Nullable
    public Int2ObjectMap<TradeOffers.Factory[]> getTrade(Identifier id) {
        return trades.get(id);
    }

    /**
     * Handles the prepared intermediate object.
     *
     * <p>This method is called in the apply executor, or the game engine, in a
     * reload.
     *
     * @param prepared the prepared object
     * @param manager  the resource manager
     * @param profiler the apply profiler
     */
    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        loadTradeFactoryMap();
        ImmutableMap.Builder<Identifier, Int2ObjectMap<TradeOffers.Factory[]>> builder = ImmutableMap.builder();
        prepared.forEach(
                (identifier, jsonElement) -> {
                    try {
                        final JsonArray[] levels = GSON.fromJson(jsonElement, JsonArray[].class);
                        builder.put(identifier, parseTrades(levels));
                    } catch (Exception ex) {
                        LOGGER.error("Couldn't parse trade data {}", identifier, ex);
                    }
                }
        );
        this.trades = builder.build();
        LOGGER.info("Loaded {} trades", trades.size());
    }

    private void loadTradeFactoryMap() {
        FACTORYNAME_TO_CLASS.clear();
        // Custom trade factories
        loadTradeFactories(VF_EnchantBookFactory.class, VF_TradeItemFactory.class, VF_LootTableFactory.class);
        // Vanilla trade factories
        TradeOffers.PROFESSION_TO_LEVELED_TRADE.values().forEach(map -> map.int2ObjectEntrySet().stream()
                .map(Map.Entry::getValue).forEach(factories -> Arrays.stream(factories)
                        .map(TradeOffers.Factory::getClass).forEach(TradeManager::loadTradeFactory)));
        TradeOffers.WANDERING_TRADER_TRADES.values().forEach(factories -> Arrays.stream(factories)
                .map(TradeOffers.Factory::getClass).forEach(TradeManager::loadTradeFactory));
    }

    private Int2ObjectMap<TradeOffers.Factory[]> parseTrades(JsonArray[] levels) {
        Int2ObjectMap<TradeOffers.Factory[]> trades = new Int2ObjectArrayMap<>();
        int level = 1;
        for (JsonArray jsonLevelArray : levels) {
            TradeOffers.Factory[] factories = new TradeOffers.Factory[jsonLevelArray.size()];
            int offer = 0;
            for (JsonElement jsonTradeFactory : jsonLevelArray) {
                factories[offer] = parseTradeOffer(jsonTradeFactory);
                offer++;
            }
            trades.put(level, factories);
            level++;
        }
        return trades;
    }

    @SafeVarargs
    public static void loadTradeFactories(Class<? extends TradeOffers.Factory>... classes) {
        for (Class<? extends TradeOffers.Factory> clazz : classes) {
            loadTradeFactory(clazz);
        }
    }

    public static void loadTradeFactory(Class<? extends TradeOffers.Factory> clazz) {
        final String className = clazz.getName();
        final String deobfuscated = Deobfuscator.deobfuscate(className);
        boolean isObfuscated = !deobfuscated.equals(className);
        if (isObfuscated) {
            final String humanReadable = deobfuscated.replaceAll(REGEX, "$1");
            FACTORYNAME_TO_CLASS.put(humanReadable, clazz);
        } else {
            FACTORYNAME_TO_CLASS.put(clazz.getSimpleName(), clazz);
        }
    }


    private TradeOffers.Factory parseTradeOffer(JsonElement factoryJson) {
        final JsonObject jsonObject = factoryJson.getAsJsonObject();
        final String type = jsonObject.get("type").getAsString();
        final Class<? extends TradeOffers.Factory> clazz = FACTORYNAME_TO_CLASS.get(type);
        if (clazz == null) {
            VillagerFix.LOGGER.info(FACTORYNAME_TO_CLASS);
            throw new IllegalArgumentException("Unknown trade type \"" + type + "\"");
        }
        jsonObject.remove("type");
        final Object obj = GSON.fromJson(jsonObject.toString(), clazz);
        if (obj instanceof TradeOffers.Factory factory) {
            return factory;
        } else {
            // This should never happen
            throw new UnknownError();
        }
    }

}
