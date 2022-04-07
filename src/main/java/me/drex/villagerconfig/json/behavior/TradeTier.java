package me.drex.villagerconfig.json.behavior;

import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.gen.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class TradeTier {

    final TradeGroup[] groups;
    final TradeOffers.Factory[] trades;
    final int total_exp_required;

    protected static final TradeTier EMPTY = new TradeTier(Integer.MAX_VALUE, null, null);

    public TradeTier(int total_exp_required, @Nullable TradeGroup[] groups, @Nullable TradeOffers.Factory[] trades) {
        this.total_exp_required = total_exp_required;
        this.groups = groups;
        this.trades = trades;
    }

    protected TradeOffers.Factory[] getTradeOffers(AbstractRandom random) {
        List<TradeOffers.Factory> trades = new LinkedList<>();
        if (this.groups != null) {
            for (TradeGroup group : this.groups) {
                trades.addAll(List.of(group.getTrades(random)));
            }
        }
        if (this.trades != null) {
            trades.addAll(List.of(this.trades));
        }
        return trades.toArray(new TradeOffers.Factory[]{});
    }

    protected int getRequiredExperience() {
        return this.total_exp_required;
    }

    protected void validate(TradeTableReporter reporter) {
        if (groups != null) {
            for (int i = 0; i < groups.length; i++) {
                TradeGroup group = groups[i];
                group.validate(reporter.makeChild(".groups[" + i + "]"));
            }
        }
        if (trades != null) {
            for (int i = 0; i < trades.length; i++) {
                TradeOffers.Factory tradeFactory = trades[i];
                if (tradeFactory instanceof IValidate validate) {
                    validate.validate(reporter.makeChild(".trades[" + i + "]"));
                }
            }
        }
    }

    @Override
    public String toString() {
        return "TradeTier{" +
                "groups=" + Arrays.toString(groups) +
                ", total_exp_required=" + total_exp_required +
                '}';
    }
}
