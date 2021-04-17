package me.drex.villagerfix.util;

import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.ConfigEntries;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.item.Item;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;

import java.util.*;

public class Helper {

    public static boolean chance(double percentage) {
        return percentage >= new Random().nextDouble() * 100;
    }

    public static TradeOffers.Factory[] removeBlackListedItems(TradeOffers.Factory[] pool, MerchantEntity entity) {
        List<TradeOffers.Factory> list = new ArrayList<>(Arrays.asList(pool));
        Iterator<TradeOffers.Factory> iterator = list.iterator();
        while (iterator.hasNext()) {
            TradeOffers.Factory factory = iterator.next();
            TradeOffer tradeOffer = factory.create(entity, entity.getRandom());
            if (shouldRemove(tradeOffer)) iterator.remove();
        }
        return list.toArray(new TradeOffers.Factory[0]);
    }

    public static boolean shouldRemove(TradeOffer tradeOffer) {
        for (String string : ConfigEntries.features.blacklistedTrades) {
            Item item = ItemHelper.toItem(string);
            if (item == null) {
                VillagerFix.LOG.error("Unable to parse " + string + " to item.");
                continue;
            }
            if (tradeOffer == null ||
                    item == tradeOffer.getOriginalFirstBuyItem().getItem() ||
                    item == tradeOffer.getSecondBuyItem().getItem() ||
                    item == tradeOffer.getSellItem().getItem()) {
                return true;
            }
        }
        return false;
    }

}
