package me.drex.villagerconfig.data;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.mixin.MerchantOfferAccessor;
import me.drex.villagerconfig.util.loot.VCLootContextParams;
import net.minecraft.Util;
import net.minecraft.core.component.DataComponentPredicate;
import net.minecraft.core.component.TypedDataComponent;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditions;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BehaviorTrade implements VillagerTrades.ItemListing {

    public static final Codec<BehaviorTrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        LootPoolEntries.CODEC.fieldOf("cost_a").forGetter(behaviorTrade -> behaviorTrade.costA),
        LootPoolEntries.CODEC.optionalFieldOf("cost_b").forGetter(behaviorTrade -> behaviorTrade.costB),
        LootPoolEntries.CODEC.fieldOf("result").forGetter(behaviorTrade -> behaviorTrade.result),
        NumberProviders.CODEC.optionalFieldOf("price_multiplier", ConstantValue.exactly(0.2f)).forGetter(behaviorTrade -> behaviorTrade.priceMultiplier),
        NumberProviders.CODEC.optionalFieldOf("trader_experience", ConstantValue.exactly(0)).forGetter(behaviorTrade -> behaviorTrade.traderExperience),
        NumberProviders.CODEC.optionalFieldOf("max_uses", ConstantValue.exactly(12)).forGetter(behaviorTrade -> behaviorTrade.maxUses),
        LootItemConditions.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(behaviorTrade -> behaviorTrade.conditions),
        Codec.unboundedMap(Codec.STRING, NumberProviders.CODEC).optionalFieldOf("reference_providers", Map.of()).forGetter(behaviorTrade -> behaviorTrade.referenceProviders),
        Codec.BOOL.optionalFieldOf("reward_experience", true).forGetter(behaviorTrade -> behaviorTrade.rewardExperience)
    ).apply(instance, BehaviorTrade::new));

    private final LootPoolEntryContainer costA;
    private final Optional<LootPoolEntryContainer> costB;
    private final LootPoolEntryContainer result;
    private final NumberProvider priceMultiplier;
    private final NumberProvider traderExperience;
    private final NumberProvider maxUses;
    protected final Predicate<LootContext> compositeCondition;
    private final List<LootItemCondition> conditions;
    private final Map<String, NumberProvider> referenceProviders;
    private final boolean rewardExperience;

    BehaviorTrade(LootPoolEntryContainer costA, Optional<LootPoolEntryContainer> costB, LootPoolEntryContainer result, NumberProvider priceMultiplier, NumberProvider traderExperience, NumberProvider maxUses, List<LootItemCondition> conditions, Map<String, NumberProvider> referenceProviders, boolean rewardExperience) {
        this.costA = costA;
        this.costB = costB;
        this.result = result;
        this.priceMultiplier = priceMultiplier;
        this.traderExperience = traderExperience;
        this.maxUses = maxUses;
        this.conditions = conditions;
        this.compositeCondition = Util.allOf(conditions);
        this.referenceProviders = referenceProviders;
        this.rewardExperience = rewardExperience;
    }

    @Nullable
    @Override
    public MerchantOffer getOffer(Entity entity, RandomSource random) {

        LootParams lootParams = new LootParams.Builder((ServerLevel) entity.level())
            .withParameter(LootContextParams.ORIGIN, entity.position())
            .withParameter(LootContextParams.THIS_ENTITY, entity)
            .withParameter(VCLootContextParams.NUMBER_REFERENCE, generateNumberReferences(entity, random))
            .create(VCLootContextParams.VILLAGER_LOOT_CONTEXT);
        LootContext lootContext = new LootContext.Builder(lootParams).create(Optional.empty());

        AtomicReference<ItemStack> costA = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> costB = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        // Result needs to be generated first, because it's number references may be required for the costs
        addRandomItem(result::set, lootContext, this.result);
        this.costB.ifPresent(container -> addRandomItem(costB::set, lootContext, container));
        addRandomItem(costA::set, lootContext, this.costA);

        Optional<ItemCost> itemCostB = Optional.empty();
        if (this.costB.isPresent()) {
            itemCostB = Optional.of(convertToCost(costB.get()));
        }
        MerchantOffer tradeOffer = new MerchantOffer(
            convertToCost(costA.get()),
            itemCostB,
            result.get(),
            maxUses.getInt(lootContext),
            traderExperience.getInt(lootContext),
            priceMultiplier.getFloat(lootContext)
        );
        ((MerchantOfferAccessor) tradeOffer).setRewardExp(rewardExperience);
        return tradeOffer;
    }

    private static ItemCost convertToCost(ItemStack stack) {
        ItemCost itemCost = new ItemCost(stack.getItem(), stack.getCount());
        itemCost.withComponents(builder -> {
            for (TypedDataComponent<?> component : stack.getComponents()) {
                addToBuilder(builder, component);
            }
            return builder;
        });
        return itemCost;
    }

    private static <T> void addToBuilder(DataComponentPredicate.Builder builder, TypedDataComponent<T> type) {
        builder.expect(type.type(), type.value());
    }

    // Copied from LootPool.addRandomItem()
    private void addRandomItem(Consumer<ItemStack> consumer, LootContext lootContext, LootPoolEntryContainer lootPoolEntryContainer) {
        RandomSource randomSource = lootContext.getRandom();
        ArrayList<LootPoolEntry> entries = Lists.newArrayList();
        MutableInt totalWeight = new MutableInt();
        lootPoolEntryContainer.expand(lootContext, lootPoolEntry -> {
            int weight = lootPoolEntry.getWeight(lootContext.getLuck());
            if (weight > 0) {
                entries.add(lootPoolEntry);
                totalWeight.add(weight);
            }
        });
        int size = entries.size();
        if (totalWeight.intValue() == 0 || size == 0) {
            return;
        }
        if (size == 1) {
            entries.get(0).createItemStack(consumer, lootContext);
            return;
        }
        int j = randomSource.nextInt(totalWeight.intValue());
        for (LootPoolEntry lootPoolEntry : entries) {
            if ((j -= lootPoolEntry.getWeight(lootContext.getLuck())) >= 0) continue;
            lootPoolEntry.createItemStack(consumer, lootContext);
            return;
        }
    }

    private Map<String, Float> generateNumberReferences(Entity entity, RandomSource random) {
        LootParams lootParams = new LootParams.Builder((ServerLevel) entity.level())
            .create(LootContextParamSets.EMPTY);
        LootContext simpleContext = new LootContext.Builder(lootParams).create(Optional.empty());
        return referenceProviders.entrySet().stream().collect(
            Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFloat(simpleContext))
        );
    }

    public static class Builder {

        private final LootPoolEntryContainer costA;
        private Optional<LootPoolEntryContainer> costB = Optional.empty();
        private final LootPoolEntryContainer result;
        private NumberProvider priceMultiplier = ConstantValue.exactly(0.2F);
        private NumberProvider traderExperience = ConstantValue.exactly(1);
        private NumberProvider maxUses = ConstantValue.exactly(12);
        private final List<LootItemCondition> conditions = Lists.newArrayList();
        private final Map<String, NumberProvider> referenceProviders = new HashMap<>();
        private boolean rewardExperience = true;

        public Builder(LootPoolEntryContainer.Builder<?> costA, LootPoolEntryContainer.Builder<?> result) {
            this.costA = costA.build();
            this.result = result.build();
        }

        public Builder(LootPoolEntryContainer.Builder<?> costA, LootPoolEntryContainer.Builder<?> costB, LootPoolEntryContainer.Builder<?> result) {
            this.costA = costA.build();
            this.costB = Optional.of(costB.build());
            this.result = result.build();
        }

        public Builder priceMultiplier(float priceMultiplier) {
            return priceMultiplier(ConstantValue.exactly(priceMultiplier));
        }

        public Builder priceMultiplier(NumberProvider priceMultiplier) {
            this.priceMultiplier = priceMultiplier;
            return this;
        }

        public Builder traderExperience(float traderExp) {
            return traderExperience(ConstantValue.exactly(traderExp));
        }

        public Builder traderExperience(NumberProvider traderExp) {
            this.traderExperience = traderExp;
            return this;
        }

        public Builder when(LootItemCondition.Builder builder) {
            this.conditions.add(builder.build());
            return this;
        }

        public Builder maxUses(float maxUses) {
            return maxUses(ConstantValue.exactly(maxUses));
        }

        public Builder maxUses(NumberProvider maxUses) {
            this.maxUses = maxUses;
            return this;
        }

        public Builder numberReference(String id, NumberProvider numberProvider) {
            this.referenceProviders.put(id, numberProvider);
            return this;
        }

        public Builder rewardExperience(boolean rewardExp) {
            this.rewardExperience = rewardExp;
            return this;
        }

        public BehaviorTrade build() {
            return new BehaviorTrade(costA, costB, result, priceMultiplier, traderExperience, maxUses, this.conditions, referenceProviders, rewardExperience);
        }

    }

}
