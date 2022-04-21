package me.drex.villagerconfig.json.behavior;

import com.google.common.collect.Sets;
import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.util.math.random.AbstractRandom;
import net.minecraft.village.TradeOffers;

import java.util.Arrays;
import java.util.HashSet;

public class TradeGroup implements IValidate {

    Integer num_to_select;
    final TradeOffers.Factory[] trades;

    public TradeGroup(Integer num_to_select, TradeOffers.Factory[] trades) {
        this.num_to_select = num_to_select;
        this.trades = trades;
    }

    public TradeOffers.Factory[] getTrades(AbstractRandom random) {
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

    public void validate(TradeTableReporter reporter) {
        if (trades == null) {
            reporter.error("Missing trades[]");
        } else {
            if (trades.length == 0) {
                reporter.warn("trades[] is empty");
            }
            for (int i = 0; i < trades.length; i++) {
                TradeOffers.Factory tradeFactory = trades[i];
                if (tradeFactory instanceof IValidate validate) {
                    validate.validate(reporter.makeChild(".trades[" + i + "]"));
                }
            }
            this.num_to_select = this.num_to_select != null ? this.num_to_select : 2;
        }
    }

    @Override
    public String toString() {
        return "TradeGroup{" +
                "num_to_select=" + num_to_select +
                ", trades=" + Arrays.toString(trades) +
                '}';
    }

}
