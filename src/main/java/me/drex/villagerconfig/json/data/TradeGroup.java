package me.drex.villagerconfig.json.data;

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.lang.reflect.Type;
import java.util.HashSet;

public class TradeGroup {

    final int numToSelect;
    final VillagerTrades.ItemListing[] trades;

    public TradeGroup(int numToSelect, VillagerTrades.ItemListing[] trades) {
        this.numToSelect = numToSelect;
        this.trades = trades;
    }

    public VillagerTrades.ItemListing[] getTrades(RandomSource random) {
        HashSet<Integer> set = Sets.newHashSet();
        if (trades.length > numToSelect) {
            while (set.size() < numToSelect) {
                set.add(random.nextInt(trades.length));
            }
        } else {
            return trades;
        }
        VillagerTrades.ItemListing[] factories = new VillagerTrades.ItemListing[set.size()];
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
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "trade group");
            VillagerTrades.ItemListing[] trades = GsonHelper.getAsObject(jsonObject, "trades", context, VillagerTrades.ItemListing[].class);
            int numToSelect = GsonHelper.getAsInt(jsonObject, "num_to_select");
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
