package me.drex.villagerconfig.util.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import com.mojang.logging.LogUtils;
import me.drex.villagerconfig.util.loot.LootContextTypes;
import me.drex.villagerconfig.util.loot.LootFunctionTypes;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.random.Random;
import org.slf4j.Logger;

import java.util.*;
import java.util.stream.Stream;

public class EnchantRandomlyLootFunction extends ConditionalLootFunction {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final List<Enchantment> include;
    private final List<Enchantment> exclude;
    private final boolean tradeEnchantments;

    EnchantRandomlyLootFunction(LootCondition[] conditions, Collection<Enchantment> include, Collection<Enchantment> exclude, boolean tradeEnchantments) {
        super(conditions);
        this.include = ImmutableList.copyOf(include);
        this.exclude = ImmutableList.copyOf(exclude);
        this.tradeEnchantments = tradeEnchantments;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
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
        Stream<Enchantment> stream = Registries.ENCHANTMENT.stream();
        if (!this.exclude.isEmpty()) {
            stream = stream.filter(enchantment -> !this.exclude.contains(enchantment));
        }
        if (tradeEnchantments) {
            stream = stream.filter(Enchantment::isAvailableForEnchantedBookOffer);
        }
        boolean isBook = stack.isOf(Items.BOOK);
        return stream.filter(enchantment -> isBook || enchantment.isAcceptableItem(stack)).toList();
    }

    private static ItemStack addEnchantmentToStack(ItemStack stack, Enchantment enchantment, Random random, LootContext context) {
        int level = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
        if (stack.isOf(Items.BOOK)) {
            stack = new ItemStack(Items.ENCHANTED_BOOK);
            EnchantedBookItem.addEnchantment(stack, new EnchantmentLevelEntry(enchantment, level));
        } else {
            stack.addEnchantment(enchantment, level);
        }
        if (context.hasParameter(LootContextTypes.NUMBER_REFERENCE)) {
            Map<String, Float> referenceProviders = context.get(LootContextTypes.NUMBER_REFERENCE);
            referenceProviders.put("enchantmentLevel", (float) level);
            referenceProviders.put("treasureMultiplier", enchantment.isTreasure() ? (float) 2 : 1);
        }
        return stack;
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.ENCHANT_RANDOMLY;
    }

    public static class Builder
            extends ConditionalLootFunction.Builder<EnchantRandomlyLootFunction.Builder> {

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
        public LootFunction build() {
            return new EnchantRandomlyLootFunction(this.getConditions(), include, exclude, tradeEnchantments);
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }
    }

    public static class Serializer
            extends ConditionalLootFunction.Serializer<EnchantRandomlyLootFunction> {
        @Override
        public void toJson(JsonObject jsonObject, EnchantRandomlyLootFunction enchantRandomlyLootFunction, JsonSerializationContext jsonSerializationContext) {
            super.toJson(jsonObject, enchantRandomlyLootFunction, jsonSerializationContext);
            addEnchantments(jsonObject, enchantRandomlyLootFunction.include, "include");
            addEnchantments(jsonObject, enchantRandomlyLootFunction.exclude, "exclude");
            jsonObject.addProperty("trade_enchantments", enchantRandomlyLootFunction.tradeEnchantments);
        }

        private static void addEnchantments(JsonObject jsonObject, List<Enchantment> enchantments, String key) {
            if (!enchantments.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (Enchantment enchantment : enchantments) {
                    Identifier identifier = Registries.ENCHANTMENT.getId(enchantment);
                    if (identifier == null) {
                        throw new IllegalArgumentException("Don't know how to serialize enchantment " + enchantment);
                    }
                    jsonArray.add(new JsonPrimitive(identifier.toString()));
                }
                jsonObject.add(key, jsonArray);
            }
        }

        @Override
        public EnchantRandomlyLootFunction fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext, LootCondition[] lootConditions) {
            List<Enchantment> include = getEnchantments(jsonObject, "include");
            List<Enchantment> exclude = getEnchantments(jsonObject, "exclude");
            boolean tradeEnchantments = JsonHelper.getBoolean(jsonObject, "trade_enchantments", false);
            return new EnchantRandomlyLootFunction(lootConditions, include, exclude, tradeEnchantments);
        }

        private static List<Enchantment> getEnchantments(JsonObject jsonObject, String key) {
            ArrayList<Enchantment> enchantments = Lists.newArrayList();
            if (jsonObject.has(key)) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, key);
                for (JsonElement jsonElement : jsonArray) {
                    String string = JsonHelper.asString(jsonElement, "enchantment");
                    Enchantment enchantment = Registries.ENCHANTMENT.getOrEmpty(new Identifier(string)).orElseThrow(() -> new JsonSyntaxException("Unknown enchantment '" + string + "'"));
                    enchantments.add(enchantment);
                }
            }
            return enchantments;
        }

    }

}
