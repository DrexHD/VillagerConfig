package me.drex.villagerconfig.json;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.drex.villagerconfig.json.data.BehaviorTrade;
import me.drex.villagerconfig.json.data.TradeGroup;
import me.drex.villagerconfig.json.data.TradeTable;
import me.drex.villagerconfig.json.data.TradeTier;
import net.minecraft.loot.LootGsons;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;
import net.minecraft.village.TradeOffers;

public class TradeGsons {

    public static final Gson GSON = LootGsons.getTableGsonBuilder()
            .setPrettyPrinting()
            .registerTypeAdapter(TradeTable.class, new TradeTable.Serializer())
            .registerTypeAdapter(TradeTier.class, new TradeTier.Serializer())
            .registerTypeAdapter(TradeGroup.class, new TradeGroup.Serializer())
            .registerTypeHierarchyAdapter(TradeOffers.Factory.class, new BehaviorTrade.Serializer())
            .create();

}
