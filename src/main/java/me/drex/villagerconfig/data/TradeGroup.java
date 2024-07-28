package me.drex.villagerconfig.data;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.util.loot.VCLootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

public class TradeGroup {

    public static final Codec<TradeGroup> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        NumberProviders.CODEC.fieldOf("num_to_select").forGetter(tradeGroup -> tradeGroup.numToSelect),
        BehaviorTrade.CODEC.listOf().fieldOf("trades").forGetter(tradeGroup -> tradeGroup.trades)
    ).apply(instance, TradeGroup::new));

    final NumberProvider numToSelect;
    final List<BehaviorTrade> trades;

    public TradeGroup(NumberProvider numToSelect, List<BehaviorTrade> trades) {
        this.numToSelect = numToSelect;
        this.trades = trades;
    }

    public List<BehaviorTrade> getTrades(AbstractVillager villager) {
        LootParams lootParams = new LootParams.Builder((ServerLevel) villager.level())
            .withParameter(LootContextParams.ORIGIN, villager.position())
            .withParameter(LootContextParams.THIS_ENTITY, villager)
            .withParameter(VCLootContextParams.NUMBER_REFERENCE, Collections.emptyMap())
            .create(VCLootContextParams.VILLAGER_LOOT_CONTEXT);
        LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());

        List<BehaviorTrade> applicableTrades = trades.stream().filter(behaviorTrade -> behaviorTrade.compositeCondition.test(lootContext)).toList();

        HashSet<Integer> set = Sets.newHashSet();
        int count = numToSelect.getInt(lootContext);
        if (applicableTrades.size() > count) {
            while (set.size() < count) {
                set.add(lootContext.getRandom().nextInt(applicableTrades.size()));
            }
        } else {
            return applicableTrades;
        }
        BehaviorTrade[] factories = new BehaviorTrade[set.size()];
        int index = 0;
        for (Integer integer : set) {
            factories[index] = applicableTrades.get(integer);
            index++;
        }
        return List.of(factories);
    }

}
