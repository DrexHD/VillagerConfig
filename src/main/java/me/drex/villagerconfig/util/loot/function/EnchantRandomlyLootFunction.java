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
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EnchantRandomlyLootFunction extends LootItemConditionalFunction {

    private static final Logger LOGGER = LogUtils.getLogger();

    public static final MapCodec<EnchantRandomlyLootFunction> CODEC = RecordCodecBuilder.mapCodec(
        instance -> commonFields(instance)
            .and(
                instance.group(
                    RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("include")
                        .forGetter(enchantRandomlyFunction -> enchantRandomlyFunction.include),
                    RegistryCodecs.homogeneousList(Registries.ENCHANTMENT).optionalFieldOf("exclude")
                        .forGetter(enchantRandomlyFunction -> enchantRandomlyFunction.exclude),
                    Codec.INT.optionalFieldOf("min_level", 0).forGetter(enchantRandomlyLootFunction -> enchantRandomlyLootFunction.minLevel),
                    Codec.INT.optionalFieldOf("max_level", Integer.MAX_VALUE).forGetter(enchantRandomlyLootFunction -> enchantRandomlyLootFunction.maxLevel)
                )
            )
            .apply(instance, EnchantRandomlyLootFunction::new)
    );

    private final Optional<HolderSet<Enchantment>> include;
    private final Optional<HolderSet<Enchantment>> exclude;
    private final int minLevel;
    private final int maxLevel;

    EnchantRandomlyLootFunction(List<LootItemCondition> conditions, Optional<HolderSet<Enchantment>> include, Optional<HolderSet<Enchantment>> exclude, int minLevel, int maxLevel) {
        super(conditions);
        this.include = include;
        this.exclude = exclude;
        this.minLevel = minLevel;
        this.maxLevel = maxLevel;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, LootContext context) {
        RandomSource randomSource = context.getRandom();
        Optional<Holder<Enchantment>> optional = this.include.flatMap(holders -> holders.getRandomElement(randomSource)).or(
            () -> {
                boolean isBook = stack.is(Items.BOOK);
                HolderSet<Enchantment> excluded = exclude.orElse(HolderSet.direct());
                List<Holder.Reference<Enchantment>> list = context.getLevel().registryAccess().registryOrThrow(Registries.ENCHANTMENT).holders()
                    .filter(reference -> isBook || reference.value().canEnchant(stack))
                    .filter(reference -> !excluded.contains(reference))
                    .toList();
                return Util.getRandomSafe(list, randomSource);
            }
        );
        if (optional.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
            return stack;
        } else {
            return enchantItem(stack, optional.get(), randomSource, context);
        }
    }

    private ItemStack enchantItem(ItemStack itemStack, Holder<Enchantment> holder, RandomSource randomSource, LootContext context) {
        Enchantment enchantment = holder.value();
        int level = Mth.nextInt(randomSource, enchantment.getMinLevel(), enchantment.getMaxLevel());
        level = Mth.clamp(level, this.minLevel, this.maxLevel);
        if (itemStack.is(Items.BOOK)) {
            itemStack = new ItemStack(Items.ENCHANTED_BOOK);
        }
        itemStack.enchant(holder, level);
        if (context.hasParam(VCLootContextParams.NUMBER_REFERENCE)) {
            Map<String, Float> referenceProviders = context.getParamOrNull(VCLootContextParams.NUMBER_REFERENCE);
            referenceProviders.put("enchantmentLevel", (float) level);
            referenceProviders.put("treasureMultiplier", holder.is(EnchantmentTags.DOUBLE_TRADE_PRICE) ? (float) 2 : 1);
        }
        return itemStack;
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return LootItemFunctionTypes.ENCHANT_RANDOMLY;
    }

    public static class Builder
        extends LootItemConditionalFunction.Builder<EnchantRandomlyLootFunction.Builder> {

        private Optional<HolderSet<Enchantment>> include = Optional.empty();
        private Optional<HolderSet<Enchantment>> exclude = Optional.empty();
        private int minLevel = 0;
        private int maxLevel = Integer.MAX_VALUE;

        public Builder include(HolderSet<Enchantment> enchantments) {
            this.include = Optional.of(enchantments);
            return this;
        }

        public Builder exclude(HolderSet<Enchantment> enchantments) {
            this.exclude = Optional.of(enchantments);
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

        @Override
        public @NotNull LootItemFunction build() {
            return new EnchantRandomlyLootFunction(this.getConditions(), include, exclude, minLevel, maxLevel);
        }

        @Override
        protected @NotNull Builder getThis() {
            return this;
        }
    }

}
