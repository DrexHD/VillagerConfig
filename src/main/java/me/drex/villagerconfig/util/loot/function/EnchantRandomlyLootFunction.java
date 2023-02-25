package me.drex.villagerconfig.util.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import me.drex.villagerconfig.util.loot.LootContextParams;
import me.drex.villagerconfig.util.loot.LootItemFunctionTypes;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.Mth;
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

import java.util.*;
import java.util.stream.Stream;

public class EnchantRandomlyLootFunction extends LootItemConditionalFunction {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final List<Enchantment> include;
    private final List<Enchantment> exclude;
    private final boolean tradeEnchantments;

    EnchantRandomlyLootFunction(LootItemCondition[] conditions, Collection<Enchantment> include, Collection<Enchantment> exclude, boolean tradeEnchantments) {
        super(conditions);
        this.include = ImmutableList.copyOf(include);
        this.exclude = ImmutableList.copyOf(exclude);
        this.tradeEnchantments = tradeEnchantments;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, LootContext context) {
        Random random = context.getRandom();
        List<Enchantment> enchantments = getEnchantments(stack);
        if (enchantments.isEmpty()) {
            LOGGER.warn("Couldn't find a compatible enchantment for {}", stack);
            return stack;
        }
        Enchantment enchantment = enchantments.get(random.nextInt(enchantments.size()));
        return addEnchantmentToStack(stack, enchantment, random, context);
    }

    private List<Enchantment> getEnchantments(ItemStack stack) {
        if (!this.include.isEmpty()) {
            return this.include;
        }
        Stream<Enchantment> stream = Registry.ENCHANTMENT.stream();
        if (!this.exclude.isEmpty()) {
            stream = stream.filter(enchantment -> !this.exclude.contains(enchantment));
        }
        if (tradeEnchantments) {
            stream = stream.filter(Enchantment::isTradeable);
        }
        boolean isBook = stack.is(Items.BOOK);
        return stream.filter(enchantment -> isBook || enchantment.canEnchant(stack)).toList();
    }

    private static ItemStack addEnchantmentToStack(ItemStack stack, Enchantment enchantment, Random random, LootContext context) {
        int level = Mth.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        if (stack.is(Items.BOOK)) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack, new EnchantmentInstance(enchantment, level));
        } else {
            stack.enchant(enchantment, level);
        }
        if (context.hasParam(LootContextParams.NUMBER_REFERENCE)) {
            Map<String, Float> referenceProviders = context.getParamOrNull(LootContextParams.NUMBER_REFERENCE);
            referenceProviders.put("enchantmentLevel", (float) level);
            referenceProviders.put("treasureMultiplier", enchantment.isTreasureOnly() ? (float) 2 : 1);
        }
        return stack;
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return LootItemFunctionTypes.ENCHANT_RANDOMLY;
    }

    public static class Builder
            extends LootItemConditionalFunction.Builder<EnchantRandomlyLootFunction.Builder> {

        private final Set<Enchantment> include = Sets.newHashSet();
        private final Set<Enchantment> exclude = Sets.newHashSet();
        private boolean tradeEnchantments = false;

        public Builder include(Enchantment enchantment) {
            this.include.add(enchantment);
            return this;
        }

        public Builder exclude(Enchantment enchantment) {
            this.exclude.add(enchantment);
            return this;
        }

        public Builder tradeEnchantments() {
            this.tradeEnchantments = true;
            return this;
        }

        @Override
        public @NotNull LootItemFunction build() {
            return new EnchantRandomlyLootFunction(this.getConditions(), include, exclude, tradeEnchantments);
        }

        @Override
        protected @NotNull Builder getThis() {
            return this;
        }
    }

    public static class Serializer
            extends LootItemConditionalFunction.Serializer<EnchantRandomlyLootFunction> {

        @Override
        public void serialize(@NotNull JsonObject jsonObject, @NotNull EnchantRandomlyLootFunction enchantRandomlyLootFunction, @NotNull JsonSerializationContext jsonSerializationContext) {
            super.serialize(jsonObject, enchantRandomlyLootFunction, jsonSerializationContext);
            addEnchantments(jsonObject, enchantRandomlyLootFunction.include, "include");
            addEnchantments(jsonObject, enchantRandomlyLootFunction.exclude, "exclude");
            jsonObject.addProperty("trade_enchantments", enchantRandomlyLootFunction.tradeEnchantments);        }

        private static void addEnchantments(JsonObject jsonObject, List<Enchantment> enchantments, String key) {
            if (!enchantments.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Enchantment enchantment : enchantments) {
                    ResourceLocation identifier = Registry.ENCHANTMENT.getKey(enchantment);
                    if (identifier == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
                    }
                    jsonArray.add(new JsonPrimitive(identifier.toString()));
                }
                jsonObject.add(key, jsonArray);
            }
        }

        @Override
        public @NotNull EnchantRandomlyLootFunction deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext context, LootItemCondition @NotNull [] lootConditions) {
            List<Enchantment> include = getEnchantments(jsonObject, "include");
            List<Enchantment> exclude = getEnchantments(jsonObject, "exclude");
            boolean tradeEnchantments = GsonHelper.getAsBoolean(jsonObject, "trade_enchantments", false);
            return new EnchantRandomlyLootFunction(lootConditions, include, exclude, tradeEnchantments);
        }

        private static List<Enchantment> getEnchantments(JsonObject jsonObject, String key) {
            ArrayList<Enchantment> enchantments = Lists.newArrayList();
            if (jsonObject.has(key)) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, key);
                for (JsonElement jsonElement : jsonArray) {
                    String string = GsonHelper.convertToString(jsonElement, "enchantment");
                    Enchantment enchantment = Registry.ENCHANTMENT.getOptional(new ResourceLocation(string)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
                    enchantments.add(enchantment);
                }
            }
            return enchantments;
        }

    }

}
