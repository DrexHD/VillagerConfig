package me.drex.villagerconfig.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.json.TradeGsons;
import me.drex.villagerconfig.json.behavior.TradeGroup;
import me.drex.villagerconfig.json.behavior.TradeTable;
import me.drex.villagerconfig.json.behavior.TradeTier;
import me.drex.villagerconfig.mixin.VillagerDataAccessor;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;

import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.VILLAGER;
import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.WANDERING_TRADER;

public class TradeProvider implements DataProvider {

    private static final Logger LOGGER = VillagerConfig.LOGGER;
    private final Gson gson;
    public static final Identifier WANDERING_TRADER_ID = new Identifier("wanderingtrader");
    private static final IntUnaryOperator WANDERING_TRADER_COUNT = i -> switch (i) {
        case 1 -> 5;
        case 2 -> 1;
        default -> 0;
    };

    private final DataGenerator root;

    public TradeProvider(DynamicRegistryManager registryManager, DataGenerator root) {
        this.root = root;
        this.gson = TradeGsons.getTradeGsonBuilder(registryManager).setPrettyPrinting().create();
    }

    @Override
    public void run(DataWriter cache) {
        // Save all villager trades
        for (VillagerProfession villagerProfession : Registry.VILLAGER_PROFESSION) {
            this.saveMerchantTrades(cache, Registry.VILLAGER_PROFESSION.getId(villagerProfession), TradeOffers.PROFESSION_TO_LEVELED_TRADE.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>()), VILLAGER);
        }
        // Save wandering trader trades
        this.saveMerchantTrades(cache, WANDERING_TRADER_ID, TradeOffers.WANDERING_TRADER_TRADES, WANDERING_TRADER);
    }

    private void saveMerchantTrades(DataWriter cache, Identifier merchantId, Int2ObjectMap<TradeOffers.Factory[]> trades, OfferCountType offerCountType) {
        Path path = getOutput(this.root.getOutput(), merchantId);
        JsonElement jsonElements;
        try {
            jsonElements = serializeData(trades, offerCountType);
        } catch (Exception e) {
            LOGGER.error("Couldn't serialize trade data {}", path, e);
            return;
        }
        try {
            DataProvider.writeToPath(cache, jsonElements, path);
        } catch (IOException e) {
            LOGGER.error("Couldn't save trade data {}", path, e);
        }
    }

    @Override
    public String getName() {
        return "Trades";
    }

    private static Path getOutput(Path rootOutput, Identifier merchantId) {
        return rootOutput.resolve("data/" + merchantId.getNamespace() + "/trades/" + merchantId.getPath() + ".json");
    }

    private JsonElement serializeData(Int2ObjectMap<TradeOffers.Factory[]> trades, OfferCountType offerCountType) {
        int levels = trades.size();
        final TradeTier[] tiers = new TradeTier[levels];
        trades.forEach((level, factoryArr) -> {
            TradeGroup tradeGroup = new TradeGroup(offerCountType.getOfferCount(level), factoryArr);
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

}
