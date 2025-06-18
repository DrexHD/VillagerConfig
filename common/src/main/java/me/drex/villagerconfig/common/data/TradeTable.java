package me.drex.villagerconfig.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.List;

public class TradeTable {

    public static final Codec<TradeTable> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        TradeTier.CODEC.listOf().fieldOf("tiers").forGetter(tradeTable -> tradeTable.tiers)
    ).apply(instance, TradeTable::new));

    final List<TradeTier> tiers;

    public TradeTable(List<TradeTier> tiers) {
        this.tiers = tiers;
    }

    private TradeTier getTradeTier(int level) {
        if (level < 1) throw new IllegalArgumentException("Villager level must at least be 1");
        if (tiers.size() >= level) {
            return tiers.get(level - 1);
        }
        return TradeTier.EMPTY;
    }

    public VillagerTrades.ItemListing[] getTradeOffers(AbstractVillager villager, int level) {
        TradeTier tradeTier = getTradeTier(level);
        return tradeTier.getTradeOffers(villager);
    }

    public int requiredExperience(int level) {
        return getTradeTier(level).requiredExperience();
    }

    public int maxLevel() {
        return tiers.size();
    }

}
