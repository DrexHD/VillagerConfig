package me.drex.villagerfix.villager;

import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.entry.AbstractMod;
import me.drex.villagerfix.util.Helper;
import me.drex.villagerfix.villager.types.JsonSerializer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapIcon;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
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
                if (o instanceof JSONArray) {
                    JSONArray jsonArray1 = (JSONArray) o;
                    List<TradeOffers.Factory> tradeOffers = new ArrayList<>();
                    for (Object tradeOffer : jsonArray1) {
                        try {
                            tradeOffers.add(parse((JSONObject) tradeOffer));
                        } catch (Exception e) {
                            VillagerFix.LOG.error("There was an error initializing villager trading json data", e);
                        }
                    }
                    data.add(tradeOffers);
                } else {
                    VillagerFix.LOG.warn("Unable to parse " + o);
                }
            }
        } catch (Exception e) {
            VillagerFix.LOG.error("Couldn't load " + fileName + ".json" + e);
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

    private TradeOffers.Factory parse(JSONObject jsonObject) {
        TradeOffers.Factory factory;
        switch (jsonObject.getString("type")) {
            case "BuyForOneEmerald": {
                factory = new TradeOffers.BuyForOneEmeraldFactory(Helper.toItem(jsonObject.getString("buy")), jsonObject.getInt("price"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
                break;
            }
            case "EnchantBook": {
                factory = new TradeOffers.EnchantBookFactory(jsonObject.getInt("experience"));
                break;
            }
            case "ProcessItem": {
                ItemStack secondBuy = JsonSerializer.parseItemStack(jsonObject.getJSONObject("secondBuy"));
                ItemStack sellItem = JsonSerializer.parseItemStack(jsonObject.getJSONObject("sell"));
                factory = new TradeOffers.ProcessItemFactory(secondBuy.getItem(), secondBuy.getCount(), jsonObject.getInt("price"), sellItem.getItem(), sellItem.getCount(), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
                break;
            }
            case "SellDyedArmor": {
                factory = new TradeOffers.SellDyedArmorFactory(Helper.toItem(jsonObject.getString("sell")), jsonObject.getInt("price"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
                break;
            }
            case "SellEnchantedTool": {
                factory = new TradeOffers.SellEnchantedToolFactory(Helper.toItem(jsonObject.getString("tool")), jsonObject.getInt("basePrice"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"), jsonObject.getInt("multiplier"));
                break;
            }
            case "SellItem": {
                factory = new TradeOffers.SellItemFactory(new ItemStack(Helper.toItem(jsonObject.getString("sell"))), jsonObject.getInt("price"), jsonObject.getInt("count"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
                break;
            }
            case "SellMap": {
                factory = new TradeOffers.SellMapFactory(jsonObject.getInt("price"), Registry.STRUCTURE_FEATURE.get(new Identifier(jsonObject.getString("structure"))), jsonObject.getEnum(MapIcon.Type.class, "iconType"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
                break;
            }
            case "SellPotionHoldingItem": {
                ItemStack secondBuy = JsonSerializer.parseItemStack(jsonObject.getJSONObject("secondBuy"));
                ItemStack sellItem = JsonSerializer.parseItemStack(jsonObject.getJSONObject("sell"));
                factory = new TradeOffers.SellPotionHoldingItemFactory(secondBuy.getItem(), secondBuy.getCount(), sellItem.getItem(), sellItem.getCount(), jsonObject.getInt("price"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
                break;
            }
            case "SellSuspiciousStew": {
                factory = new TradeOffers.SellSuspiciousStewFactory(Registry.STATUS_EFFECT.get(new Identifier(jsonObject.getString("effect"))), jsonObject.getInt("duration"), jsonObject.getInt("experience"));
                break;
            }
            case "TypeAwareBuyForOneEmerald": {
                Map<VillagerType, Item> map = new HashMap<>();
                JSONObject jsonMap = jsonObject.getJSONObject("map");
                for (String key : jsonMap.keySet()) {
                    map.put(Registry.VILLAGER_TYPE.get(new Identifier(key)), Helper.toItem(jsonMap.getString(key)));
                }
                factory = new TradeOffers.TypeAwareBuyForOneEmeraldFactory(jsonObject.getInt("count"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"), map);
                break;
            }
            case "custom": {
                factory = (entity, random) -> new TradeOffer(JsonSerializer.parseItemStack(jsonObject.getJSONObject("firstBuy")), JsonSerializer.parseItemStack(jsonObject.getJSONObject("secondBuy")), JsonSerializer.parseItemStack(jsonObject.getJSONObject("sell")), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"), jsonObject.getInt("multiplier"));
                break;
            }
            default:
                throw new IllegalStateException("Unexpected value: " + jsonObject.getString("type"));
        }
        return factory;
    }

    public Int2ObjectMap<TradeOffers.Factory[]> build() {
        Int2ObjectMap<TradeOffers.Factory[]> result = new Int2ObjectArrayMap<>();
        for (int i = 0; i < data.size(); i++) {
            result.put(i + 1, data.get(i).toArray(new TradeOffers.Factory[0]));
        }
        return result;
    }

}
