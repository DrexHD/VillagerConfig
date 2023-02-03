package me.drex.villagerconfig.json.data;

import com.google.gson.*;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;

import java.lang.reflect.Type;

public class TradeTable {

    final TradeTier[] tiers;

    public TradeTable(TradeTier[] tiers) {
        this.tiers = tiers;
    }

    private TradeTier getTradeTier(int level) {
        if (level < 1) throw new IllegalArgumentException("Villager level must at least be 1");
        if (tiers.length >= level) {
            return tiers[level - 1];
        }
        return TradeTier.EMPTY;
    }

    public TradeOffers.Factory[] getTradeOffers(int level, Random random) {
        if (random == null) throw new IllegalArgumentException("Random must not be null");
        TradeTier tradeTier = getTradeTier(level);
        return tradeTier.getTradeOffers(random);
    }

    public int requiredExperience(int level) {
        return getTradeTier(level).requiredExperience();
    }

    public int maxLevel() {
        return tiers.length;
    }

    public static class Serializer implements JsonSerializer<TradeTable>, JsonDeserializer<TradeTable> {

        @Override
        public TradeTable deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "trade table");
            TradeTier[] tiers = JsonHelper.deserialize(jsonObject, "tiers", context, TradeTier[].class);
            return new TradeTable(tiers);
        }

        @Override
        public JsonElement serialize(TradeTable tradeTable, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("tiers", context.serialize(tradeTable.tiers));
            return jsonObject;
        }
    }

}
