package me.drex.villagerfix.json.behavior;

import com.google.common.collect.Sets;
import net.minecraft.village.TradeOffers;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;

public class TradeGroup {

    final int num_to_select;
    final TradeOffers.Factory[] trades;

    public TradeGroup(int num_to_select, TradeOffers.Factory[] trades) {
        this.num_to_select = num_to_select;
        this.trades = trades;
    }

    public TradeOffers.Factory[] getTrades(Random random) {
        HashSet<Integer> set = Sets.newHashSet();
        if (trades.length > num_to_select) {
            while (set.size() < num_to_select) {
                set.add(random.nextInt(trades.length));
            }
        } else {
            return trades;
        }
        TradeOffers.Factory[] factories = new TradeOffers.Factory[set.size()];
        int index = 0;
        for (Integer integer : set) {
            factories[index] = trades[integer];
            index++;
        }
        return factories;
    }

    @Override
    public String toString() {
        return "TradeGroup{" +
                "num_to_select=" + num_to_select +
                ", trades=" + Arrays.toString(trades) +
                '}';
    }

}
