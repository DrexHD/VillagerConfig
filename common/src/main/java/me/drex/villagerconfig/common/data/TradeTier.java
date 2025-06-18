package me.drex.villagerconfig.common.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.VillagerTrades;

import java.util.LinkedList;
import java.util.List;

public class TradeTier {

    public static final Codec<TradeTier> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        Codec.INT.fieldOf("total_exp_required").forGetter(tradeTier -> tradeTier.totalExpRequired),
        TradeGroup.CODEC.listOf().fieldOf("groups").forGetter(tradeTier -> tradeTier.groups)
    ).apply(instance, TradeTier::new));

    final int totalExpRequired;
    final List<TradeGroup> groups;

    protected static final TradeTier EMPTY = new TradeTier(Integer.MAX_VALUE, List.of());

    public TradeTier(int totalExpRequired, List<TradeGroup> groups) {
        this.totalExpRequired = totalExpRequired;
        this.groups = groups;
    }

    protected VillagerTrades.ItemListing[] getTradeOffers(AbstractVillager villager) {
        List<VillagerTrades.ItemListing> trades = new LinkedList<>();
        if (this.groups != null) {
            for (TradeGroup group : this.groups) {
                trades.addAll(group.getTrades(villager));
            }
        }
        return trades.toArray(VillagerTrades.ItemListing[]::new);
    }

    protected int requiredExperience() {
        return this.totalExpRequired;
    }

}
