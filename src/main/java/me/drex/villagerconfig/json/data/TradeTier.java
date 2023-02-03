package me.drex.villagerconfig.json.data;

import com.google.gson.*;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;

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

    protected TradeOffers.Factory[] getTradeOffers(Random random) {
        List<TradeOffers.Factory> trades = new LinkedList<>();
        if (this.groups != null) {
            for (TradeGroup group : this.groups) {
                trades.addAll(List.of(group.getTrades(random)));
            }
        }
        return trades.toArray(new TradeOffers.Factory[]{});
    }

    protected int requiredExperience() {
        return this.totalExpRequired;
    }

    public static class Serializer implements JsonSerializer<TradeTier>, JsonDeserializer<TradeTier> {

        @Override
        public TradeTier deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "trade tier");
            TradeGroup[] groups = JsonHelper.deserialize(jsonObject, "groups", context, TradeGroup[].class);
            int totalExpRequired = JsonHelper.getInt(jsonObject, "total_exp_required");
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
