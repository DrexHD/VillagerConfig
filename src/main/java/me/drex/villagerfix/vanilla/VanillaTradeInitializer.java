package me.drex.villagerfix.vanilla;

import me.drex.villagerfix.api.TradeEntry;
import me.drex.villagerfix.api.TradeFactoryStorage;
import me.drex.villagerfix.api.VillagerFixAPI;
import me.drex.villagerfix.mixin.accessor.*;
import me.drex.villagerfix.util.Helper;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.map.MapIcon;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerType;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import static me.drex.villagerfix.util.Helper.parseItemStack;

public class VanillaTradeInitializer implements VillagerFixAPI {
    
    @Override
    public void onInitialize(TradeFactoryStorage storage) {
        storage.registerTradeFactory(TradeOffers.BuyForOneEmeraldFactory.class, new TradeEntry(jsonObject -> new TradeOffers.BuyForOneEmeraldFactory(Helper.toItem(jsonObject.getString("buy")), jsonObject.getInt("price"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience")), factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof BuyForOneEmeraldFactoryAccessor accessor) {
                jsonObject.put("buy", Helper.toName(accessor.getBuy()));
                jsonObject.put("price", accessor.getPrice());
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.EnchantBookFactory.class, new TradeEntry(jsonObject -> new TradeOffers.EnchantBookFactory(jsonObject.getInt("experience")), factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof EnchantBookFactoryAccessor accessor) {
                jsonObject.put("experience", accessor.getExperience());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.ProcessItemFactory.class, new TradeEntry(jsonObject -> {
            ItemStack secondBuy = parseItemStack(jsonObject.getJSONObject("secondBuy"));
            ItemStack sellItem = parseItemStack(jsonObject.getJSONObject("sell"));
            return new TradeOffers.ProcessItemFactory(secondBuy.getItem(), secondBuy.getCount(), jsonObject.getInt("price"), sellItem.getItem(), sellItem.getCount(), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
        }, factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof ProccessItemFactoryAccessor accessor) {
                jsonObject.put("secondBuy", parseItemStack(accessor.getSecondBuy()));
                jsonObject.put("price", accessor.getPrice());
                jsonObject.put("sell", parseItemStack(accessor.getSell()));
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
                jsonObject.put("multiplier", accessor.getMultiplier());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.SellDyedArmorFactory.class, new TradeEntry(jsonObject -> new TradeOffers.SellDyedArmorFactory(Helper.toItem(jsonObject.getString("sell")), jsonObject.getInt("price"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience")), factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof SellDyedArmorFactoryAccessor accessor) {
                jsonObject.put("sell", Helper.toName(accessor.getSell()));
                jsonObject.put("price", accessor.getPrice());
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.SellEnchantedToolFactory.class, new TradeEntry(jsonObject -> new TradeOffers.SellEnchantedToolFactory(Helper.toItem(jsonObject.getString("tool")), jsonObject.getInt("basePrice"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"), jsonObject.getInt("multiplier")), factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof SellEnchantedToolFactoryAccessor accessor) {
                jsonObject.put("tool", Helper.toName(accessor.getTool().getItem()));
                jsonObject.put("basePrice", accessor.getBasePrice());
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
                jsonObject.put("multiplier", accessor.getMultiplier());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.SellItemFactory.class, new TradeEntry(jsonObject -> new TradeOffers.SellItemFactory(new ItemStack(Helper.toItem(jsonObject.getString("sell"))), jsonObject.getInt("price"), jsonObject.getInt("count"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"), jsonObject.getFloat("multiplier")), factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof SellItemFactoryAccessor accessor) {
                jsonObject.put("sell", Helper.toName(accessor.getSell().getItem()));
                jsonObject.put("price", accessor.getPrice());
                jsonObject.put("count", accessor.getCount());
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
                jsonObject.put("multiplier", accessor.getMultiplier());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.SellMapFactory.class, new TradeEntry(jsonObject -> new TradeOffers.SellMapFactory(jsonObject.getInt("price"), Registry.STRUCTURE_FEATURE.get(new Identifier(jsonObject.getString("structure"))), jsonObject.getEnum(MapIcon.Type.class, "iconType"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience")), factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof SellMapFactoryAccessor accessor) {
                jsonObject.put("price", accessor.getPrice());
                jsonObject.put("structure", Registry.STRUCTURE_FEATURE.getId(accessor.getStructure()).toString());
                jsonObject.put("iconType", accessor.getType());
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.SellPotionHoldingItemFactory.class, new TradeEntry(jsonObject -> {
            ItemStack secondBuy = parseItemStack(jsonObject.getJSONObject("secondBuy"));
            ItemStack sellItem = parseItemStack(jsonObject.getJSONObject("sell"));
            return new TradeOffers.SellPotionHoldingItemFactory(secondBuy.getItem(), secondBuy.getCount(), sellItem.getItem(), sellItem.getCount(), jsonObject.getInt("price"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"));
        }, factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof SellPotionHoldingItemFactoryAccessor accessor) {
                jsonObject.put("sell", parseItemStack(accessor.getSell()));
                jsonObject.put("price", accessor.getPrice());
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
                jsonObject.put("secondBuy", parseItemStack(new ItemStack(accessor.getSecondBuy())));
                jsonObject.put("priceMultiplier", accessor.getPriceMultiplier());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.SellSuspiciousStewFactory.class, new TradeEntry(jsonObject -> new TradeOffers.SellSuspiciousStewFactory(Registry.STATUS_EFFECT.get(new Identifier(jsonObject.getString("effect"))), jsonObject.getInt("duration"), jsonObject.getInt("experience")), factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof SellSuspiciousStewFactoryAccessor accessor) {
                jsonObject.put("effect", Registry.STATUS_EFFECT.getId(accessor.getEffect()));
                jsonObject.put("duration", accessor.getDuration());
                jsonObject.put("experience", accessor.getExperience());
                jsonObject.put("multiplier", accessor.getMultiplier());
            }
            return jsonObject;
        }));
        storage.registerTradeFactory(TradeOffers.TypeAwareBuyForOneEmeraldFactory.class, new TradeEntry(jsonObject -> {
            Map<VillagerType, Item> map = new HashMap<>();
            JSONObject jsonMap = jsonObject.getJSONObject("map");
            for (String key : jsonMap.keySet()) {
                map.put(Registry.VILLAGER_TYPE.get(new Identifier(key)), Helper.toItem(jsonMap.getString(key)));
            }
            return new TradeOffers.TypeAwareBuyForOneEmeraldFactory(jsonObject.getInt("count"), jsonObject.getInt("max_uses"), jsonObject.getInt("experience"), map);
        }, factory -> {
            JSONObject jsonObject = new JSONObject();
            if (factory instanceof TypeAwareBuyForOneEmeraldFactoryAccessor accessor) {
                JSONObject map = new JSONObject();
                for (Map.Entry<VillagerType, Item> entry : accessor.getMap().entrySet()) {
                    map.put(Registry.VILLAGER_TYPE.getId(entry.getKey()).toString(), Helper.toName(entry.getValue()));
                }
                jsonObject.put("map", map);
                jsonObject.put("count", accessor.getCount());
                jsonObject.put("max_uses", accessor.getMaxUses());
                jsonObject.put("experience", accessor.getExperience());
            }
            return jsonObject;
        }));
    }


}
