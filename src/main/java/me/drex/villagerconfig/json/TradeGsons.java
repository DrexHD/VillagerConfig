package me.drex.villagerconfig.json;

import com.google.gson.Gson;
import me.drex.villagerconfig.json.data.BehaviorTrade;
import me.drex.villagerconfig.json.data.TradeGroup;
import me.drex.villagerconfig.json.data.TradeTable;
import me.drex.villagerconfig.json.data.TradeTier;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.storage.loot.Deserializers;

public class TradeGsons {

    public static final Gson GSON = Deserializers.createLootTableSerializer()
            .setPrettyPrinting()
            .registerTypeAdapter(TradeTable.class, new TradeTable.Serializer())
            .registerTypeAdapter(TradeTier.class, new TradeTier.Serializer())
            .registerTypeAdapter(TradeGroup.class, new TradeGroup.Serializer())
            .registerTypeHierarchyAdapter(VillagerTrades.ItemListing.class, new BehaviorTrade.Serializer())
            .create();

}
