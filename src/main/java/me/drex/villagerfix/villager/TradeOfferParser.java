package me.drex.villagerfix.villager;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TradeOfferParser {

    public static final Map<String, TradeOfferParser> cache = new HashMap<>();
    private final List<List<TradeOffers.Factory>> data = new ArrayList<>();

    private TradeOfferParser(String fileName, Int2ObjectMap<TradeOffers.Factory[]> original) {
        cache.put(fileName, this);
        Path path = VillagerFix.DATA_PATH.resolve(fileName + ".json");
        try {
            JSONArray input = new JSONArray(new String(Files.readAllBytes(path)));
            int level = 1;
            for (Object o : input) {
                if (o instanceof JSONArray jsonArray) {
                    List<TradeOffers.Factory> tradeOffers = new ArrayList<>();
                    int trade = 0;
                    for (Object tradeOffer : jsonArray) {
                        try {
                            TradeOffers.Factory factory = VillagerFix.data.deserialize((JSONObject) tradeOffer);
                            if (factory != null) {
                                tradeOffers.add(factory);
                            } else {
                                TradeOffers.Factory[] factories = original.get(level);
                                if (factories != null) {
                                    if (factories.length > trade) {
                                        tradeOffers.add(factories[trade]);
                                    }
                                }
                            }
                        } catch (Exception e) {
                            VillagerFix.LOGGER.error("There was an error initializing villager trading json data", e);
                        }
                        trade++;
                    }
                    data.add(tradeOffers);
                } else {
                    VillagerFix.LOGGER.warn("Unable to parse " + o);
                }
                level++;
            }
        } catch (Exception e) {
            VillagerFix.LOGGER.error("Couldn't load " + fileName + ".json" + e);
        }
    }

    TradeOfferParser() {
    }

    public static TradeOfferParser of(VillagerProfession profession, Int2ObjectMap<TradeOffers.Factory[]> original) {
        if (profession == VillagerProfession.NONE) return new TradeOfferParser();
        return of(profession.toString(), original);
    }

    public static TradeOfferParser of(String fileName, Int2ObjectMap<TradeOffers.Factory[]> original) {
        if (cache.containsKey(fileName)) {
            return cache.get(fileName);
        } else {
            return new TradeOfferParser(fileName, original);
        }
    }

    public Int2ObjectMap<TradeOffers.Factory[]> build() {
        Int2ObjectMap<TradeOffers.Factory[]> result = new Int2ObjectArrayMap<>();
        for (int i = 0; i < data.size(); i++) {
            result.put(i + 1, data.get(i).toArray(new TradeOffers.Factory[0]));
        }
        return result;
    }

}
