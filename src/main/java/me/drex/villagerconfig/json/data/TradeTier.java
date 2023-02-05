package me.drex.villagerconfig.json.data;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.lang.reflect.Type;
import java.util.LinkedList;
import java.util.List;

public class TradeTier {

    final int totalExpRequired;
    final TradeGroup[] groups;

    protected static final TradeTier EMPTY = new TradeTier(Integer.MAX_VALUE, new TradeGroup[]{});

    public TradeTier(int totalExpRequired, TradeGroup[] groups) {
        this.totalExpRequired = totalExpRequired;
        this.groups = groups;
    }

    protected VillagerTrades.ItemListing[] getTradeOffers(RandomSource random) {
        List<VillagerTrades.ItemListing> trades = new LinkedList<>();
        if (this.groups != null) {
            for (TradeGroup group : this.groups) {
                trades.addAll(List.of(group.getTrades(random)));
            }
        }
        return trades.toArray(new VillagerTrades.ItemListing[]{});
    }

    protected int requiredExperience() {
        return this.totalExpRequired;
    }

    public static class Serializer implements JsonSerializer<TradeTier>, JsonDeserializer<TradeTier> {

        @Override
        public TradeTier deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "trade tier");
            TradeGroup[] groups = GsonHelper.getAsObject(jsonObject, "groups", context, TradeGroup[].class);
            int totalExpRequired = GsonHelper.getAsInt(jsonObject, "total_exp_required");
            return new TradeTier(totalExpRequired, groups);
        }

        @Override
        public JsonElement serialize(TradeTier tradeTier, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("groups", context.serialize(tradeTier.groups));
            jsonObject.addProperty("total_exp_required", tradeTier.totalExpRequired);
            return jsonObject;
        }
    }

}
