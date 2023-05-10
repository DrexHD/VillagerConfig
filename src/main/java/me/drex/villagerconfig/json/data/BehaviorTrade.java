package me.drex.villagerconfig.json.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.drex.villagerconfig.mixin.MerchantOfferAccessor;
import me.drex.villagerconfig.util.loot.VCLootContextParams;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.EmptyLootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BehaviorTrade implements VillagerTrades.ItemListing {

    private final LootPoolEntryContainer costA;
    private final LootPoolEntryContainer costB;
    private final LootPoolEntryContainer result;
    private final NumberProvider priceMultiplier;
    private final NumberProvider traderExperience;
    private final NumberProvider maxUses;
    private final Map<String, NumberProvider> referenceProviders;
    private final boolean rewardExperience;

    BehaviorTrade(LootPoolEntryContainer costA, LootPoolEntryContainer costB, LootPoolEntryContainer result, NumberProvider priceMultiplier, NumberProvider traderExperience, NumberProvider maxUses, Map<String, NumberProvider> referenceProviders, boolean rewardExperience) {
        this.costA = costA;
        this.costB = costB;
        this.result = result;
        this.priceMultiplier = priceMultiplier;
        this.traderExperience = traderExperience;
        this.maxUses = maxUses;
        this.referenceProviders = referenceProviders;
        this.rewardExperience = rewardExperience;
    }

    @Nullable
    @Override
    public MerchantOffer getOffer(Entity entity, RandomSource random) {

        LootParams lootParams = new LootParams.Builder((ServerLevel) entity.level())
                .withOptionalParameter(LootContextParams.THIS_ENTITY, entity)
                .withParameter(VCLootContextParams.NUMBER_REFERENCE, generateNumberReferences(entity, random))
                .create(VCLootContextParams.VILLAGER_LOOT_CONTEXT);
        LootContext lootContext = new LootContext.Builder(lootParams).create(LootTable.DEFAULT_RANDOM_SEQUENCE);

        AtomicReference<ItemStack> costA = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> costB = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        // Result needs to be generated first, because it's number references may be required for the costs
        this.result.expand(lootContext, lootChoice -> lootChoice.createItemStack(result::set, lootContext));
        this.costB.expand(lootContext, lootChoice -> lootChoice.createItemStack(costB::set, lootContext));
        this.costA.expand(lootContext, lootChoice -> lootChoice.createItemStack(costA::set, lootContext));

        MerchantOffer tradeOffer = new MerchantOffer(
                costA.get(),
                costB.get(),
                result.get(),
                maxUses.getInt(lootContext),
                traderExperience.getInt(lootContext),
                priceMultiplier.getFloat(lootContext)
        );
        ((MerchantOfferAccessor) tradeOffer).setRewardExp(rewardExperience);
        return tradeOffer;
    }

    private Map<String, Float> generateNumberReferences(Entity entity, RandomSource random) {
        LootParams lootParams = new LootParams.Builder((ServerLevel) entity.level())
                .create(LootContextParamSets.EMPTY);
        LootContext simpleContext = new LootContext.Builder(lootParams).create(LootTable.DEFAULT_RANDOM_SEQUENCE);
        return referenceProviders.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().getFloat(simpleContext))
        );
    }

    public static class Builder {

        private final LootPoolEntryContainer costA;
        private LootPoolEntryContainer costB = EmptyLootItem.emptyItem().build();
        private final LootPoolEntryContainer result;
        private NumberProvider priceMultiplier = ConstantValue.exactly(0.2F);
        private NumberProvider traderExperience = ConstantValue.exactly(1);
        private NumberProvider maxUses = ConstantValue.exactly(12);
        private final Map<String, NumberProvider> referenceProviders = new HashMap<>();
        private boolean rewardExperience = true;

        public Builder(LootPoolEntryContainer.Builder<?> costA, LootPoolEntryContainer.Builder<?> result) {
            this.costA = costA.build();
            this.result = result.build();
        }

        public Builder(LootPoolEntryContainer.Builder<?> costA, LootPoolEntryContainer.Builder<?> costB, LootPoolEntryContainer.Builder<?> result) {
            this.costA = costA.build();
            this.costB = costB.build();
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
            return new BehaviorTrade(costA, costB, result, priceMultiplier, traderExperience, maxUses, referenceProviders, rewardExperience);
        }

    }

    public static class Serializer implements JsonSerializer<BehaviorTrade>, JsonDeserializer<BehaviorTrade> {

        private static final Type REFERENCE_PROVIDERS_TYPE = TypeToken.getParameterized(Map.class, String.class, NumberProvider.class).getType();

        @Override
        public BehaviorTrade deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = GsonHelper.convertToJsonObject(jsonElement, "behaviour trade");
            LootPoolEntryContainer costA = GsonHelper.getAsObject(jsonObject, "cost_a", context, LootPoolEntryContainer.class);
            LootPoolEntryContainer costB = GsonHelper.getAsObject(jsonObject, "cost_b", EmptyLootItem.emptyItem().build(), context, LootPoolEntryContainer.class);
            LootPoolEntryContainer result = GsonHelper.getAsObject(jsonObject, "result", context, LootPoolEntryContainer.class);
            NumberProvider priceMultiplier = GsonHelper.getAsObject(jsonObject, "price_multiplier", ConstantValue.exactly(0.2f), context, NumberProvider.class);
            NumberProvider traderExperience = GsonHelper.getAsObject(jsonObject, "trader_experience", ConstantValue.exactly(0), context, NumberProvider.class);
            NumberProvider maxUses = GsonHelper.getAsObject(jsonObject, "max_uses", ConstantValue.exactly(12), context, NumberProvider.class);
            Map<String, NumberProvider> referenceProviders = Collections.emptyMap();
            if (jsonObject.has("reference_providers")) {
                referenceProviders = context.deserialize(jsonObject.get("reference_providers"), REFERENCE_PROVIDERS_TYPE);
            }
            boolean rewardExperience = GsonHelper.getAsBoolean(jsonObject, "reward_experience", true);
            return new BehaviorTrade(costA, costB, result, priceMultiplier, traderExperience, maxUses, referenceProviders, rewardExperience);
        }

        @Override
        public JsonElement serialize(BehaviorTrade behaviorTrade, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("cost_a", context.serialize(behaviorTrade.costA));
            if (!(behaviorTrade.costB instanceof EmptyLootItem)) {
                jsonObject.add("cost_b", context.serialize(behaviorTrade.costB));
            }
            jsonObject.add("result", context.serialize(behaviorTrade.result));
            jsonObject.add("price_multiplier", context.serialize(behaviorTrade.priceMultiplier));
            jsonObject.add("trader_experience", context.serialize(behaviorTrade.traderExperience));
            jsonObject.add("max_uses", context.serialize(behaviorTrade.maxUses));
            if (!behaviorTrade.referenceProviders.isEmpty()) {
                jsonObject.add("reference_providers", context.serialize(behaviorTrade.referenceProviders, REFERENCE_PROVIDERS_TYPE));
            }
            jsonObject.addProperty("reward_experience", behaviorTrade.rewardExperience);
            return jsonObject;
        }
    }

}
