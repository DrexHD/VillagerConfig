package me.drex.villagerconfig.data;

import com.google.gson.*;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.lang.reflect.Type;
import java.util.List;

public class TradeTable {

    final List<TradeTier> tiers;

    public TradeTable(List<TradeTier> tiers) {
        this.tiers = tiers;
    }

    private TradeTier getTradeTier(int level) {
        if (level < 1) throw new IllegalArgumentException("Villager level must at least be 1");
        if (tiers.size() >= level) {
            return tiers.get(level - 1);
        }
        return TradeTier.EMPTY;
    }

    public VillagerTrades.ItemListing[] getTradeOffers(AbstractVillager villager, int level) {
        TradeTier tradeTier = getTradeTier(level);
        return tradeTier.getTradeOffers(villager);
    }

    public int requiredExperience(int level) {
        return getTradeTier(level).requiredExperience();
    }

    public int maxLevel() {
        return tiers.size();
    }

    public static class Serializer implements JsonSerializer<TradeTable>, JsonDeserializer<TradeTable> {

        @Override
        public TradeTable deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "trade table");
            TradeTier[] tiers = GsonHelper.getAsObject(jsonObject, "tiers", context, TradeTier[].class);
            return new TradeTable(List.of(tiers));
        }

        @Override
        public JsonElement serialize(TradeTable tradeTable, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("tiers", context.serialize(tradeTable.tiers));
            return jsonObject;
        }
    }

}
