package me.drex.villagerfix.json.adapter;

import com.google.gson.*;
import me.drex.villagerfix.factory.VF_EnchantBookFactory;
import me.drex.villagerfix.json.behavior.Trade;
import me.drex.villagerfix.util.Deobfuscator;
import net.minecraft.village.TradeOffers;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class FactoryTypeAdapter implements JsonDeserializer<TradeOffers.Factory>,
        JsonSerializer<TradeOffers.Factory> {

    private static final String REGEX = "(?:[\\w]+\\.)+[\\w]+[?:$|.]([\\w]+)";
    public static final Map<String, Class<? extends TradeOffers.Factory>> FACTORYNAME_TO_CLASS = new HashMap<>();
    public static final Map<Class<? extends TradeOffers.Factory>, String> FACTORYCLASS_TO_NAME = new HashMap<>();

    public FactoryTypeAdapter() {
        loadTradeFactoryMap();
    }

    private void loadTradeFactoryMap() {
        FACTORYNAME_TO_CLASS.clear();
        FACTORYCLASS_TO_NAME.clear();
        // Vanilla trade factories
        TradeOffers.PROFESSION_TO_LEVELED_TRADE.values().forEach(map -> map.int2ObjectEntrySet().stream()
                .map(Map.Entry::getValue).forEach(factories -> Arrays.stream(factories)
                        .map(TradeOffers.Factory::getClass).forEach(FactoryTypeAdapter::loadTradeFactory)));
        TradeOffers.WANDERING_TRADER_TRADES.values().forEach(factories -> Arrays.stream(factories)
                .map(TradeOffers.Factory::getClass).forEach(FactoryTypeAdapter::loadTradeFactory));
        // Custom trade factories
        addCustomTradeFactories(VF_EnchantBookFactory.class);
    }

    public static void addCustomTradeFactories(Class<? extends TradeOffers.Factory> clazz) {
        FACTORYNAME_TO_CLASS.put(clazz.getSimpleName(), clazz);
        FACTORYCLASS_TO_NAME.put(clazz, clazz.getSimpleName());
    }

    private static void loadTradeFactory(Class<? extends TradeOffers.Factory> clazz) {
        final String className = clazz.getName();
        final String deobfuscated = Deobfuscator.deobfuscate(className);
        final String humanReadable = deobfuscated.replaceAll(REGEX, "$1");
        FACTORYNAME_TO_CLASS.put(humanReadable, clazz);
        FACTORYCLASS_TO_NAME.put(clazz, humanReadable);
    }

    @Override
    public TradeOffers.Factory deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
        JsonElement jsonElement = json.getAsJsonObject().get("type");
        if (jsonElement != null) {
            String type = json.getAsJsonObject().get("type").getAsString();
            Class<? extends TradeOffers.Factory> aClass = FACTORYNAME_TO_CLASS.get(type);
            if (aClass == null) {
                throw new IllegalArgumentException("Found invalid factory type: " + type);
            } else {
                return context.deserialize(json, aClass);
            }
            // TODO: What if it's null
        } else {
            return context.deserialize(json, Trade.class);
        }
    }

    @Override
    public JsonElement serialize(TradeOffers.Factory src, Type typeOfSrc, JsonSerializationContext context) {
        String type = FACTORYCLASS_TO_NAME.get(src.getClass());
        // TODO: What if type is null
        JsonObject jsonObject = context.serialize(src).getAsJsonObject();
        jsonObject.addProperty("type", type);
        return jsonObject;
    }
}
