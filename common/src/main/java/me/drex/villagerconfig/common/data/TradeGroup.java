package me.drex.villagerconfig.common.data;

import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.common.config.ConfigManager;
import me.drex.villagerconfig.common.util.RandomUtil;
import me.drex.villagerconfig.common.util.loot.VCLootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Unit;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;

import java.util.*;

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

    public List<MerchantOffer> getTrades(AbstractVillager villager) {
        LootParams lootParams = new LootParams.Builder((ServerLevel) villager.level())
            .withParameter(LootContextParams.ORIGIN, villager.position())
            .withParameter(LootContextParams.THIS_ENTITY, villager)
            .withParameter(VCLootContextParams.NUMBER_REFERENCE, new HashMap<>())
            .withParameter(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED, Unit.INSTANCE)
            .create(VCLootContextParams.VILLAGER_LOOT_CONTEXT);
        LootContext.Builder builder = new LootContext.Builder(lootParams);
        if (!ConfigManager.CONFIG.features.tradeCycling) {
            builder.withOptionalRandomSeed(RandomUtil.getSeed(villager));
        }
        LootContext lootContext = builder.create(Optional.empty());

        List<MerchantOffer> applicableTrades = trades.stream()
            .map(behaviorTrade -> behaviorTrade.getOffer(lootContext))
            .filter(Objects::nonNull).toList();

        HashSet<Integer> set = Sets.newHashSet();
        int count = numToSelect.getInt(lootContext);
        if (applicableTrades.size() > count) {
            while (set.size() < count) {
                set.add(lootContext.getRandom().nextInt(applicableTrades.size()));
            }
        } else {
            return applicableTrades;
        }
        MerchantOffer[] factories = new MerchantOffer[set.size()];
        int index = 0;
        for (Integer integer : set) {
            factories[index] = applicableTrades.get(integer);
            index++;
        }
        return List.of(factories);
    }

}
