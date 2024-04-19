package me.drex.villagerconfig.util.loot.function;

import com.mojang.logging.LogUtils;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.util.loot.LootItemFunctionTypes;
import me.drex.villagerconfig.util.loot.VCLootContextParams;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.EnchantedBookItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentInstance;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchantRandomlyLootFunction extends LootItemConditionalFunction {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Codec<HolderSet<Enchantment>> ENCHANTMENT_SET_CODEC = BuiltInRegistries.ENCHANTMENT
        .holderByNameCodec()
        .listOf()
        .xmap(HolderSet::direct, holderSet -> holderSet.stream().toList());

    public static final MapCodec<EnchantRandomlyLootFunction> CODEC = RecordCodecBuilder.mapCodec(
        instance -> commonFields(instance)
            .and(
                instance.group(
                    ENCHANTMENT_SET_CODEC.optionalFieldOf("include")
                        .forGetter(enchantRandomlyFunction -> enchantRandomlyFunction.include),
                    ENCHANTMENT_SET_CODEC.optionalFieldOf("exclude")
                        .forGetter(enchantRandomlyFunction -> enchantRandomlyFunction.exclude),
                    Codec.INT.optionalFieldOf("min_level", 0).forGetter(enchantRandomlyLootFunction -> enchantRandomlyLootFunction.minLevel),
                    Codec.INT.optionalFieldOf("max_level", Integer.MAX_VALUE).forGetter(enchantRandomlyLootFunction -> enchantRandomlyLootFunction.maxLevel),
                    Codec.BOOL.fieldOf("trade_enchantments").orElse(false).forGetter(enchantRandomlyLootFunction -> enchantRandomlyLootFunction.tradeEnchantments)
                )
            )
            .apply(instance, EnchantRandomlyLootFunction::new)
    );

    private final Optional<HolderSet<Enchantment>> include;
    private final Optional<HolderSet<Enchantment>> exclude;
    private final int minLevel;
    private final int maxLevel;
    private final boolean tradeEnchantments;

    EnchantRandomlyLootFunction(List<LootItemCondition> conditions, Optional<HolderSet<Enchantment>> include, Optional<HolderSet<Enchantment>> exclude, int minLevel, int maxLevel, boolean tradeEnchantments) {
        super(conditions);
        this.include = include;
        this.exclude = exclude;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
        this.tradeEnchantments = tradeEnchantments;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, LootContext context) {
        RandomSource randomSource = context.getRandom();
        Optional<Holder<Enchantment>> optional = this.include.flatMap(holders -> holders.getRandomElement(randomSource)).or(
            () -> {
                boolean isBook = stack.is(Items.BOOK);
                HolderSet<Enchantment> excluded = exclude.orElse(HolderSet.direct());
                List<Holder.Reference<Enchantment>> list = BuiltInRegistries.ENCHANTMENT
                    .holders()
                    .filter(reference -> reference.value().isEnabled(context.getLevel().enabledFeatures()))
                    .filter(reference -> reference.value().isDiscoverable())
                    .filter(reference -> isBook || reference.value().canEnchant(stack))
                    .filter(reference -> !excluded.contains(reference))
                    .filter(reference -> {
                        if (tradeEnchantments) {
                            return reference.value().isTradeable();
                        }
                        return false;
                    })
                    .toList();
                return Util.getRandomSafe(list, randomSource);
            }
        );
        if (optional.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
            return stack;
        } else {
            return enchantItem(stack, (Enchantment) ((Holder<?>) optional.get()).value(), randomSource, context);
        }
    }

    private ItemStack enchantItem(ItemStack itemStack, Enchantment enchantment, RandomSource randomSource, LootContext context) {
        int level = Mth.nextInt(randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel());
        level = Mth.clamp(level, this.minLevel, this.maxLevel);
        if (itemStack.is(Items.BOOK)) {
            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        itemStack.enchant(enchantment, level);
        if (context.hasParam(VCLootContextParams.NUMBER_REFERENCE)) {
            Map<String, Float> referenceProviders = context.getParamOrNull(VCLootContextParams.NUMBER_REFERENCE);
            referenceProviders.put("enchantmentLevel", (float) level);
            referenceProviders.put("treasureMultiplier", enchantment.isTreasureOnly() ? (float) 2 : 1);
        }
        return itemStack;
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return LootItemFunctionTypes.ENCHANT_RANDOMLY;
    }

    public static class Builder
        extends LootItemConditionalFunction.Builder<EnchantRandomlyLootFunction.Builder> {

        private final List<Holder<Enchantment>> include = new ArrayList();
        private final List<Holder<Enchantment>> exclude = new ArrayList();
        private int minLevel = 0;
        private int maxLevel = Integer.MAX_VALUE;

        private boolean tradeEnchantments = false;

        public Builder include(Enchantment... enchantments) {
            for (Enchantment enchantment : enchantments) {
                this.include.add(enchantment.builtInRegistryHolder());
            }
            return this;
        }

        public Builder exclude(Enchantment... enchantments) {
            for (Enchantment enchantment : enchantments) {
                this.exclude.add(enchantment.builtInRegistryHolder());
            }
            return this;
        }

        public Builder minLevel(int minLevel) {
            this.minLevel = minLevel;
            return this;
        }

        public Builder maxLevel(int maxLevel) {
            this.maxLevel = maxLevel;
            return this;
        }

        public Builder tradeEnchantments() {
            this.tradeEnchantments = true;
            return this;
        }

        @Override
        public @NotNull LootItemFunction build() {
            return new EnchantRandomlyLootFunction(this.getConditions(), include.isEmpty() ? Optional.empty() : Optional.of(HolderSet.direct((include))), exclude.isEmpty() ? Optional.empty() : Optional.of(HolderSet.direct(exclude)), minLevel, maxLevel, tradeEnchantments);
        }

        @Override
        protected @NotNull Builder getThis() {
            return this;
        }
    }

}
