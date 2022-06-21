package me.drex.villagerconfig.json.behavior;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffers;
import org.apache.logging.log4j.Logger;

import java.util.Arrays;

public class TradeTable {

    private static final Logger LOGGER = VillagerConfig.LOGGER;
    final TradeTier[] tiers;

    public TradeTable(TradeTier[] tiers) {
        this.tiers = tiers;
    }

    private TradeTier getTradeTier(int level) {
        if (level < 1) throw new IllegalArgumentException("Villager level must at least be 1");
        if (tiers.length >= level) {
            return tiers[level - 1];
        }
        return TradeTier.EMPTY;
    }

    public TradeOffers.Factory[] getTradeOffers(int level, Random random) {
        if (random == null) throw new IllegalArgumentException("Random must not be null");
        TradeTier tradeTier = getTradeTier(level);
        return tradeTier.getTradeOffers(random);
    }

    public int getRequiredExperience(int level) {
        return getTradeTier(level).getRequiredExperience();
    }

    public int getMaxLevel() {
        return tiers.length;
    }

    public void validate(TradeTableReporter reporter) {
        for (int i = 0; i < tiers.length; i++) {
            tiers[i].validate(reporter.makeChild(".tiers[" + i + "]"));
        }
    }

    @Override
    public String toString() {
        return "TradeTable{" +
                "tiers=" + Arrays.toString(tiers) +
                '}';
    }

}
