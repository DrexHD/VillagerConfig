package me.drex.villagerconfig.util;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import me.drex.villagerconfig.json.TradeGsons;
import me.drex.villagerconfig.json.behavior.TradeGroup;
import me.drex.villagerconfig.json.behavior.TradeTable;
import me.drex.villagerconfig.json.behavior.TradeTier;
import me.drex.villagerconfig.mixin.VillagerDataAccessor;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;

import java.nio.file.Path;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;

import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.VILLAGER;
import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.WANDERING_TRADER;

public class TradeProvider implements DataProvider {

    private final DataOutput.PathResolver pathResolver;
    private final Gson gson;
    public static final Identifier WANDERING_TRADER_ID = new Identifier("wanderingtrader");
    private static final IntUnaryOperator WANDERING_TRADER_COUNT = i -> switch (i) {
        case 1 -> 5;
        case 2 -> 1;
        default -> 0;
    };

    public TradeProvider(DataOutput output, DynamicRegistryManager registryManager) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "trades");
        this.gson = TradeGsons.getTradeGsonBuilder(registryManager).setPrettyPrinting().create();
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        HashMap<Identifier, TradeData> map = Maps.newHashMap();

        // Save all villager trades
        for (VillagerProfession villagerProfession : Registries.VILLAGER_PROFESSION) {
            map.put(Registries.VILLAGER_PROFESSION.getId(villagerProfession), new TradeData(TradeOffers.PROFESSION_TO_LEVELED_TRADE.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>()), VILLAGER));
        }
        // Save wandering trader trades
        map.put(WANDERING_TRADER_ID, new TradeData(TradeOffers.WANDERING_TRADER_TRADES, WANDERING_TRADER));
        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            Identifier identifier = entry.getKey();
            TradeData tradeData = entry.getValue();
            Path path = this.pathResolver.resolveJson(identifier);
            return DataProvider.writeToPath(writer, toJson(tradeData), path);
        }).toArray(CompletableFuture[]::new));
    }
    
    @Override
    public String getName() {
        return "Trades";
    }

    private JsonElement toJson(TradeData tradeData) {
        int levels = tradeData.trades().size();
        final TradeTier[] tiers = new TradeTier[levels];
        tradeData.trades().forEach((level, factoryArr) -> {
            TradeGroup tradeGroup = new TradeGroup(tradeData.offerCountType().getOfferCount(level), factoryArr);
            tiers[level - 1] = new TradeTier((VillagerDataAccessor.getLevelBaseExperience()[level - 1]), new TradeGroup[]{tradeGroup}, null);
        });
        TradeTable tradeTable = new TradeTable(tiers);
        return gson.toJsonTree(tradeTable);
    }


    public enum OfferCountType {
        VILLAGER(i -> 2), WANDERING_TRADER(WANDERING_TRADER_COUNT);

        private final IntUnaryOperator operator;

        OfferCountType(IntUnaryOperator operator) {
            this.operator = operator;
        }

        public int getOfferCount(int level) {
            return operator.apply(level);
        }

    }

    public record TradeData(Int2ObjectMap<TradeOffers.Factory[]> trades, OfferCountType offerCountType) {

    }

}
