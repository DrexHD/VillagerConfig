package me.drex.villagerconfig.common.data;

import com.google.common.collect.Lists;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.common.mixin.MerchantOfferAccessor;
import net.minecraft.core.HolderSet;
//? if >= 26.3 {
import net.minecraft.core.registries.codec.RegistryCodecs;
//? } else {
//import net.minecraft.core.RegistryCodecs;
//? }
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.Registries;
import net.minecraft.util.Mth;
import net.minecraft.util.Util;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.trading.ItemCost;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntries;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntry;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
//? if >= 26.3 {
import net.minecraft.core.Holder;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProvider;
import net.minecraft.world.level.storage.loot.providers.number.floats.ContextFloatProviders;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProvider;
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
//? } else {
//import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
//import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
//? }
import org.apache.commons.lang3.mutable.MutableInt;
import org.jspecify.annotations.Nullable;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class BehaviorTrade {

    public static final Codec<BehaviorTrade> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        LootPoolEntries.CODEC.fieldOf("cost_a").forGetter(behaviorTrade -> behaviorTrade.costA),
        LootPoolEntries.CODEC.optionalFieldOf("cost_b").forGetter(behaviorTrade -> behaviorTrade.costB),
        LootPoolEntries.CODEC.fieldOf("result").forGetter(behaviorTrade -> behaviorTrade.result),
        //? if >= 26.3 {
        ContextFloatProviders.CODEC.optionalFieldOf("price_multiplier", ContextFloatProviders.exactly(0.2f)).forGetter(behaviorTrade -> behaviorTrade.priceMultiplier),
        //? } else {
        //NumberProviders.CODEC.optionalFieldOf("price_multiplier", ConstantValue.exactly(0.2f)).forGetter(behaviorTrade -> behaviorTrade.priceMultiplier),
        //? }
        //? if >= 26.3 {
        ContextIntProviders.CODEC.optionalFieldOf("trader_experience", ContextIntProviders.exactly(0)).forGetter(behaviorTrade -> behaviorTrade.traderExperience),
        //? } else {
        //NumberProviders.CODEC.optionalFieldOf("trader_experience", ConstantValue.exactly(0)).forGetter(behaviorTrade -> behaviorTrade.traderExperience),
        //? }
        //? if >= 26.3 {
        ContextIntProviders.CODEC.optionalFieldOf("max_uses", ContextIntProviders.exactly(12)).forGetter(behaviorTrade -> behaviorTrade.maxUses),
        //? } else {
        //NumberProviders.CODEC.optionalFieldOf("max_uses", ConstantValue.exactly(12)).forGetter(behaviorTrade -> behaviorTrade.maxUses),
        //? }
        LootItemCondition.DIRECT_CODEC.listOf().optionalFieldOf("conditions", List.of()).forGetter(behaviorTrade -> behaviorTrade.conditions),
        //? if >= 26.3 {
        Codec.unboundedMap(Codec.STRING, ContextFloatProviders.CODEC).optionalFieldOf("reference_providers", Map.of()).forGetter(behaviorTrade -> behaviorTrade.referenceProviders),
        //? } else {
        //Codec.unboundedMap(Codec.STRING, NumberProviders.CODEC).optionalFieldOf("reference_providers", Map.of()).forGetter(behaviorTrade -> behaviorTrade.referenceProviders),
        //? }
        Codec.BOOL.optionalFieldOf("reward_experience", true).forGetter(behaviorTrade -> behaviorTrade.rewardExperience),
        //? if >= 26.3 {
        RegistryCodecs.holderSet(Registries.ENCHANTMENT).optionalFieldOf("double_trade_price_enchantments").forGetter(behaviorTrade -> behaviorTrade.doubleTradePriceEnchantments)
        //? } else {
        //RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("double_trade_price_enchantments").forGetter(behaviorTrade -> behaviorTrade.doubleTradePriceEnchantments)
        //? }
    ).apply(instance, BehaviorTrade::new));

    private final LootPoolEntryContainer costA;
    private final Optional<LootPoolEntryContainer> costB;
    private final LootPoolEntryContainer result;
    //? if >= 26.3 {
    private final Holder<ContextFloatProvider> priceMultiplier;
    //? } else {
    //private final NumberProvider priceMultiplier;
    //? }
    //? if >= 26.3 {
    private final Holder<ContextIntProvider> traderExperience;
    //? } else {
    //private final NumberProvider traderExperience;
    //? }
    //? if >= 26.3 {
    private final Holder<ContextIntProvider> maxUses;
    //? } else {
    //private final NumberProvider maxUses;
    //? }
    protected final Predicate<LootContext> compositeCondition;
    private final List<LootItemCondition> conditions;
    //? if >= 26.3 {
    private final Map<String, Holder<ContextFloatProvider>> referenceProviders;
    //? } else {
    //private final Map<String, NumberProvider> referenceProviders;
    //? }
    private final boolean rewardExperience;
    private final Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments;


    BehaviorTrade(
        LootPoolEntryContainer costA, Optional<LootPoolEntryContainer> costB, LootPoolEntryContainer result,
        //? if >= 26.3 {
        Holder<ContextFloatProvider> priceMultiplier, Holder<ContextIntProvider> traderExperience, Holder<ContextIntProvider> maxUses,
        //? } else {
        //NumberProvider priceMultiplier, NumberProvider traderExperience, NumberProvider maxUses,
        //? }
        //? if >= 26.3 {
        List<LootItemCondition> conditions, Map<String, Holder<ContextFloatProvider>> referenceProviders, boolean rewardExperience,
        //? } else {
        //List<LootItemCondition> conditions, Map<String, NumberProvider> referenceProviders, boolean rewardExperience,
        //? }
        Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments
    ) {
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
        this.doubleTradePriceEnchantments = doubleTradePriceEnchantments;
    }

    @Nullable
    public MerchantOffer getOffer(LootContext lootContext) {
        if (!compositeCondition.test(lootContext)) {
            return null;
        }
        AtomicReference<ItemStack> costA = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> costB = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        // Result needs to be generated first, because it's number references may be required for the costs
        addRandomItem(result::set, lootContext, this.result);
        this.costB.ifPresent(container -> addRandomItem(costB::set, lootContext, container));
        addRandomItem(costA::set, lootContext, this.costA);

        ItemCost itemCostA = convertToCost(costA.get(), calculateAdditionalCost(result.get()));
        if (itemCostA.count() < 1) {
            return null;
        }

        Optional<ItemCost> itemCostB = Optional.empty();
        if (this.costB.isPresent()) {
            itemCostB = Optional.of(convertToCost(costB.get(), 0));
        }

        if (itemCostB.isPresent() && itemCostB.get().count() < 1) {
            return null;
        }

        if (result.get().isEmpty()) {
            return null;
        }

        MerchantOffer tradeOffer = new MerchantOffer(
            itemCostA,
            itemCostB,
            result.get(),
            //? if >= 26.3 {
            maxUses.value().getInt(lootContext),
            //? } else {
            //maxUses.getInt(lootContext),
            //? }
            //? if >= 26.3 {
            traderExperience.value().getInt(lootContext),
            //? } else {
            //traderExperience.getInt(lootContext),
            //? }
            //? if >= 26.3 {
            priceMultiplier.value().getFloat(lootContext)
            //? } else {
            //priceMultiplier.getFloat(lootContext)
            //? }
        );
        ((MerchantOfferAccessor) tradeOffer).setRewardExp(rewardExperience);
        return tradeOffer;
    }

    private int calculateAdditionalCost(ItemStack result) {
        int additionalCost = 0;

        Integer additionalTradeCost = result.remove(DataComponents.ADDITIONAL_TRADE_COST);
        if (additionalTradeCost != null) {
            additionalCost += additionalTradeCost;
        }

        if (this.doubleTradePriceEnchantments.isPresent()) {
            HolderSet<Enchantment> enchantments = this.doubleTradePriceEnchantments.get();
            ItemEnchantments itemEnchantments = result.get(DataComponents.STORED_ENCHANTMENTS);
            if (itemEnchantments != null) {
                if (itemEnchantments.keySet().stream().anyMatch(enchantments::contains)) {
                    additionalCost *= 2;
                }
            }
        }
        return additionalCost;
    }

    private static ItemCost convertToCost(ItemStack stack, int additionalCost) {
        stack.setCount(stack.count() + additionalCost);
        int count = Mth.clamp(stack.count(), 0, stack.getItem().getDefaultMaxStackSize());
        ItemCost itemCost = new ItemCost(stack.getItem(), count);
        return itemCost.withComponents(builder -> {
            //? if >= 26.3 {
            for (var component : stack.getComponentsPatch().split().added()) {
                builder.expect((DataComponentType<Object>) component.type(), component.value());
            }
            //? } else {
            /*
            for (Map.Entry<DataComponentType<?>, Optional<?>> componentPatch : stack.getComponentsPatch().entrySet()) {
                Optional<?> value = componentPatch.getValue();
                DataComponentType<?> key = componentPatch.getKey();
                value.ifPresent(o -> builder.expect((DataComponentType<Object>) key, o));
            }
            */
            //? }
            return builder;
        });
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
            entries.getFirst().createItemStack(consumer, lootContext);
            return;
        }
        int j = randomSource.nextInt(totalWeight.intValue());
        for (LootPoolEntry lootPoolEntry : entries) {
            if ((j -= lootPoolEntry.getWeight(lootContext.getLuck())) >= 0) continue;
            lootPoolEntry.createItemStack(consumer, lootContext);
            return;
        }
    }

    private Map<String, Float> generateNumberReferences(Entity entity) {
        LootParams lootParams = new LootParams.Builder((ServerLevel) entity.level())
            .create(LootContextParamSets.EMPTY);
        LootContext simpleContext = new LootContext.Builder(lootParams).create(Optional.empty());
        return referenceProviders.entrySet().stream().collect(
            //? if >= 26.3 {
            Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().value().getFloat(simpleContext))
            //? } else {
            //Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFloat(simpleContext))
            //? }
        );
    }

    public static class Builder {

        private final LootPoolEntryContainer costA;
        private Optional<LootPoolEntryContainer> costB = Optional.empty();
        private final LootPoolEntryContainer result;
        //? if >= 26.3 {
        private Holder<ContextFloatProvider> priceMultiplier = ContextFloatProviders.exactly(0.2F);
        //? } else {
        //private NumberProvider priceMultiplier = ConstantValue.exactly(0.2F);
        //? }
        //? if >= 26.3 {
        private Holder<ContextIntProvider> traderExperience = ContextIntProviders.exactly(1);
        //? } else {
        //private NumberProvider traderExperience = ConstantValue.exactly(1);
        //? }
        //? if >= 26.3 {
        private Holder<ContextIntProvider> maxUses = ContextIntProviders.exactly(12);
        //? } else {
        //private NumberProvider maxUses = ConstantValue.exactly(12);
        //? }
        private final List<LootItemCondition> conditions = Lists.newArrayList();
        //? if >= 26.3 {
        private final Map<String, Holder<ContextFloatProvider>> referenceProviders = new HashMap<>();
        //? } else {
        //private final Map<String, NumberProvider> referenceProviders = new HashMap<>();
        //? }
        private boolean rewardExperience = true;
        private Optional<HolderSet<Enchantment>> doubleTradePriceEnchantments = Optional.empty();

        public Builder(LootPoolEntryContainer.Builder<?> costA, LootPoolEntryContainer.Builder<?> result) {
            this.costA = costA.build();
            this.result = result.build();
        }

        public Builder(LootPoolEntryContainer.Builder<?> costA, LootPoolEntryContainer.Builder<?> costB, LootPoolEntryContainer.Builder<?> result) {
            this.costA = costA.build();
            this.costB = Optional.of(costB.build());
            this.result = result.build();
        }

        public Builder costB(LootPoolEntryContainer costB) {
            this.costB = Optional.of(costB);
            return this;
        }

        public Builder priceMultiplier(float priceMultiplier) {
            //? if >= 26.3 {
            return priceMultiplier(ContextFloatProviders.exactly(priceMultiplier));
            //? } else {
            //return priceMultiplier(ConstantValue.exactly(priceMultiplier));
            //? }
        }

        //? if >= 26.3 {
        public Builder priceMultiplier(Holder<ContextFloatProvider> priceMultiplier) {
        //? } else {
        //public Builder priceMultiplier(NumberProvider priceMultiplier) {
        //? }
            this.priceMultiplier = priceMultiplier;
            return this;
        }

        //? if >= 26.3 {
        public Builder traderExperience(int traderExp) {
        //? } else {
        //public Builder traderExperience(float traderExp) {
        //? }
            //? if >= 26.3 {
            return traderExperience(ContextIntProviders.exactly(traderExp));
            //? } else {
            //return traderExperience(ConstantValue.exactly(traderExp));
            //? }
        }

        //? if >= 26.3 {
        public Builder traderExperience(Holder<ContextIntProvider> traderExp) {
        //? } else {
        //public Builder traderExperience(NumberProvider traderExp) {
        //? }
            this.traderExperience = traderExp;
            return this;
        }

        //? if >= 26.3 {
        public Builder when(Holder<LootItemCondition> condition) {
            return when(condition.value());
        }
        //? }

        public Builder when(LootItemCondition.Builder builder) {
            this.conditions.add(builder.build());
            return this;
        }

        public Builder when(LootItemCondition condition) {
            this.conditions.add(condition);
            return this;
        }

        //? if >= 26.3 {
        public Builder maxUses(int maxUses) {
        //? } else {
        //public Builder maxUses(float maxUses) {
        //? }
            //? if >= 26.3 {
            return maxUses(ContextIntProviders.exactly(maxUses));
            //? } else {
            //return maxUses(ConstantValue.exactly(maxUses));
            //? }
        }

        //? if >= 26.3 {
        public Builder maxUses(Holder<ContextIntProvider> maxUses) {
        //? } else {
        //public Builder maxUses(NumberProvider maxUses) {
        //? }
            this.maxUses = maxUses;
            return this;
        }

        //? if >= 26.3 {
        public Builder numberReference(String id, Holder<ContextFloatProvider> numberProvider) {
        //? } else {
        //public Builder numberReference(String id, NumberProvider numberProvider) {
        //? }
            this.referenceProviders.put(id, numberProvider);
            return this;
        }

        public Builder rewardExperience(boolean rewardExp) {
            this.rewardExperience = rewardExp;
            return this;
        }

        public Builder doubleTradePriceEnchantments(HolderSet<Enchantment> doubleTradePriceEnchantments) {
            this.doubleTradePriceEnchantments = Optional.of(doubleTradePriceEnchantments);
            return this;
        }

        public BehaviorTrade build() {
            return new BehaviorTrade(costA, costB, result, priceMultiplier, traderExperience, maxUses, this.conditions, referenceProviders, rewardExperience, doubleTradePriceEnchantments);
        }

    }

}
