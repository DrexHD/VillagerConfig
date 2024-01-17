package me.drex.villagerconfig.data;

import com.google.common.collect.Sets;
import com.google.gson.*;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import java.lang.reflect.Type;
import java.util.HashSet;
import java.util.List;

public class TradeGroup {

    final NumberProvider numToSelect;
    final List<BehaviorTrade> trades;

    public TradeGroup(NumberProvider numToSelect, List<BehaviorTrade> trades) {
        this.numToSelect = numToSelect;
        this.trades = trades;
    }

    public List<BehaviorTrade> getTrades(AbstractVillager villager) {
        LootParams lootParams = new LootParams.Builder((ServerLevel) villager.level())
            .withOptionalParameter(LootContextParams.THIS_ENTITY, villager)
            .create(LootContextParamSets.PIGLIN_BARTER);
        LootContext lootContext = new LootContext.Builder(lootParams).create(null);

        List<BehaviorTrade> applicableTrades = trades.stream().filter(behaviorTrade -> behaviorTrade.compositeCondition.test(lootContext)).toList();

        HashSet<Integer> set = Sets.newHashSet();
        int count = numToSelect.getInt(lootContext);
        if (applicableTrades.size() > count) {
            while (set.size() < count) {
                set.add(lootContext.getRandom().nextInt(applicableTrades.size()));
            }
        } else {
            return applicableTrades;
        }
        BehaviorTrade[] factories = new BehaviorTrade[set.size()];
        int index = 0;
        for (Integer integer : set) {
            factories[index] = applicableTrades.get(integer);
            index++;
        }
        return List.of(factories);
    }

    public static class Serializer implements JsonSerializer<TradeGroup>, JsonDeserializer<TradeGroup> {

        @Override
        public TradeGroup deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "trade group");
            BehaviorTrade[] trades = GsonHelper.getAsObject(jsonObject, "trades", context, BehaviorTrade[].class);
            NumberProvider numToSelect = GsonHelper.getAsObject(jsonObject, "num_to_select", context, NumberProvider.class);
            return new TradeGroup(numToSelect, List.of(trades));
        }

        @Override
        public JsonElement serialize(TradeGroup tradeGroup, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("trades", context.serialize(tradeGroup.trades));
            jsonObject.add("num_to_select", context.serialize(tradeGroup.numToSelect));
            return jsonObject;
        }
    }

}
