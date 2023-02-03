package me.drex.villagerconfig.json.data;

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;

import java.lang.reflect.Type;
import java.util.HashSet;

public class TradeGroup {

    final int numToSelect;
    final TradeOffers.Factory[] trades;

    public TradeGroup(int numToSelect, TradeOffers.Factory[] trades) {
        this.numToSelect = numToSelect;
        this.trades = trades;
    }

    public TradeOffers.Factory[] getTrades(Random random) {
        HashSet<Integer> set = Sets.newHashSet();
        if (trades.length > numToSelect) {
            while (set.size() < numToSelect) {
                set.add(random.nextInt(trades.length));
            }
        } else {
            return trades;
        }
        TradeOffers.Factory[] factories = new TradeOffers.Factory[set.size()];
        int index = 0;
        for (Integer integer : set) {
            factories[index] = trades[integer];
            index++;
        }
        return factories;
    }

    public static class Serializer implements JsonSerializer<TradeGroup>, JsonDeserializer<TradeGroup> {

        @Override
        public TradeGroup deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "trade group");
            TradeOffers.Factory[] trades = JsonHelper.deserialize(jsonObject, "trades", context, TradeOffers.Factory[].class);
            int numToSelect = JsonHelper.getInt(jsonObject, "num_to_select");
            return new TradeGroup(numToSelect, trades);
        }

        @Override
        public JsonElement serialize(TradeGroup tradeGroup, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("trades", context.serialize(tradeGroup.trades));
            jsonObject.addProperty("num_to_select", tradeGroup.numToSelect);
            return jsonObject;
        }
    }

}
