package me.drex.villagerfix.villager;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.entry.AbstractMod;
import me.drex.villagerfix.util.Helper;
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

    private TradeOfferParser(String fileName) {
        cache.put(fileName, this);
        Path path = AbstractMod.DATA_PATH.resolve(fileName + ".json");
        try {
            JSONArray input = new JSONArray(new String(Files.readAllBytes(path)));
            for (Object o : input) {
                if (o instanceof JSONArray jsonArray) {
                    List<TradeOffers.Factory> tradeOffers = new ArrayList<>();
                    for (Object tradeOffer : jsonArray) {
                        try {
                            tradeOffers.add(AbstractMod.data.deserialize((JSONObject) tradeOffer));
                        } catch (Exception e) {
                            VillagerFix.LOGGER.error("There was an error initializing villager trading json data", e);
                        }
                    }
                    data.add(tradeOffers);
                } else {
                    VillagerFix.LOGGER.warn("Unable to parse " + o);
                }
            }
        } catch (Exception e) {
            VillagerFix.LOGGER.error("Couldn't load " + fileName + ".json" + e);
        }
    }

    TradeOfferParser() { }

    public static TradeOfferParser of(VillagerProfession profession) {
        if (profession == VillagerProfession.NONE) return new TradeOfferParser();
        return of(Helper.toName(profession));
    }

    public static TradeOfferParser of(String fileName) {
        if (cache.containsKey(fileName)) {
            return cache.get(fileName);
        } else {
            return new TradeOfferParser(fileName);
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
