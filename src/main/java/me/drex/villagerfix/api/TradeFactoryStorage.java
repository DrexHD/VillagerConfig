package me.drex.villagerfix.api;

import me.drex.villagerfix.util.Deobfuscator;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.Nullable;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class TradeFactoryStorage {

    private final Map<String, TradeEntry> data = new HashMap<>();

    public void registerTradeFactory(Class<? extends TradeOffers.Factory> clazz, TradeEntry tradeEntry) {
        data.put(formatClassName(clazz.getName()), tradeEntry);
    }

    @Nullable
    public TradeOffers.Factory deserialize(JSONObject jsonObject) {
        String type = jsonObject.getString("type");
        TradeEntry tradeEntry = data.get(type);
        if (tradeEntry != null) {
            return tradeEntry.getDeserialization().apply(jsonObject);
        }
        return null;
    }

    @Nullable
    public JSONObject serialize(TradeOffers.Factory factory) {
        String type = formatClassName(factory.getClass().getName());
        TradeEntry tradeEntry = data.get(type);
        if (tradeEntry != null) {
            JSONObject jsonObject = tradeEntry.getSerialization().apply(factory);
            jsonObject.put("type", type);
            return jsonObject;
        }
        return null;
    }

    private String formatClassName(String input) {
        return Deobfuscator.deobfuscate(input).replaceAll("(?:[\\w]+\\.)+[\\w]+[?:$|.]([\\w]+)", "$1");
    }


}
