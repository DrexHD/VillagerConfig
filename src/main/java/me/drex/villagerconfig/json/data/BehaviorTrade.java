package me.drex.villagerconfig.json.data;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;
import me.drex.villagerconfig.mixin.TradeOfferAccessor;
import me.drex.villagerconfig.util.loot.LootContextTypes;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.entry.EmptyEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.math.random.Random;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Type;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

public class BehaviorTrade implements TradeOffers.Factory {

    private final LootPoolEntry costA;
    private final LootPoolEntry costB;
    private final LootPoolEntry result;
    private final LootNumberProvider priceMultiplier;
    private final LootNumberProvider traderExperience;
    private final LootNumberProvider maxUses;
    private final Map<String, LootNumberProvider> referenceProviders;
    private final boolean rewardExperience;

    BehaviorTrade(LootPoolEntry costA, LootPoolEntry costB, LootPoolEntry result, LootNumberProvider priceMultiplier, LootNumberProvider traderExperience, LootNumberProvider maxUses, Map<String, LootNumberProvider> referenceProviders, boolean rewardExperience) {
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
    public TradeOffer create(Entity entity, Random random) {

        LootContext.Builder builder = new LootContext.Builder((ServerWorld) entity.world)
                .random(random)
                .parameter(LootContextParameters.THIS_ENTITY, entity)
                .parameter(LootContextTypes.NUMBER_REFERENCE, generateNumberReferences(entity, random));
        LootContext lootContext = builder.build(LootContextTypes.VILLAGER_LOOT_CONTEXT);

        AtomicReference<ItemStack> costA = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> costB = new AtomicReference<>(ItemStack.EMPTY);
        AtomicReference<ItemStack> result = new AtomicReference<>(ItemStack.EMPTY);
        // Result needs to be generated first, because it's number references may be required for the costs
        this.result.expand(lootContext, lootChoice -> lootChoice.generateLoot(result::set, lootContext));
        this.costB.expand(lootContext, lootChoice -> lootChoice.generateLoot(costB::set, lootContext));
        this.costA.expand(lootContext, lootChoice -> lootChoice.generateLoot(costA::set, lootContext));

        TradeOffer tradeOffer = new TradeOffer(
                costA.get(),
                costB.get(),
                result.get(),
                maxUses.nextInt(lootContext),
                traderExperience.nextInt(lootContext),
                priceMultiplier.nextFloat(lootContext)
        );
        ((TradeOfferAccessor) tradeOffer).setRewardingPlayerExperience(rewardExperience);
        return tradeOffer;
    }

    private Map<String, Float> generateNumberReferences(Entity entity, Random random) {
        LootContext.Builder simpleBuilder = new LootContext.Builder((ServerWorld) entity.world).random(random);
        LootContext simpleContext = simpleBuilder.build(net.minecraft.loot.context.LootContextTypes.EMPTY);
        return referenceProviders.entrySet().stream().collect(
                Collectors.toMap(Map.Entry::getKey, entry -> entry.getValue().nextFloat(simpleContext))
        );
    }

    public static class Builder {

        private final LootPoolEntry costA;
        private LootPoolEntry costB = EmptyEntry.builder().build();
        private final LootPoolEntry result;
        private LootNumberProvider priceMultiplier = ConstantLootNumberProvider.create(0.2F);
        private LootNumberProvider traderExperience = ConstantLootNumberProvider.create(1);
        private LootNumberProvider maxUses = ConstantLootNumberProvider.create(12);
        private final Map<String, LootNumberProvider> referenceProviders = new HashMap<>();
        private boolean rewardExperience = true;

        public Builder(LootPoolEntry.Builder<?> costA, LootPoolEntry.Builder<?> result) {
            this.costA = costA.build();
            this.result = result.build();
        }

        public Builder(LootPoolEntry.Builder<?> costA, LootPoolEntry.Builder<?> costB, LootPoolEntry.Builder<?> result) {
            this.costA = costA.build();
            this.costB = costB.build();
            this.result = result.build();
        }

        public Builder priceMultiplier(float priceMultiplier) {
            return priceMultiplier(ConstantLootNumberProvider.create(priceMultiplier));
        }

        public Builder priceMultiplier(LootNumberProvider priceMultiplier) {
            this.priceMultiplier = priceMultiplier;
            return this;
        }

        public Builder traderExperience(float traderExp) {
            return traderExperience(ConstantLootNumberProvider.create(traderExp));
        }

        public Builder traderExperience(LootNumberProvider traderExp) {
            this.traderExperience = traderExp;
            return this;
        }

        public Builder maxUses(float maxUses) {
            return maxUses(ConstantLootNumberProvider.create(maxUses));
        }

        public Builder maxUses(LootNumberProvider maxUses) {
            this.maxUses = maxUses;
            return this;
        }

        public Builder numberReference(String id, LootNumberProvider numberProvider) {
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

        private static final Type REFERENCE_PROVIDERS_TYPE = TypeToken.getParameterized(Map.class, String.class, LootNumberProvider.class).getType();

        @Override
        public BehaviorTrade deserialize(JsonElement jsonElement, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
            JsonObject jsonObject = JsonHelper.asObject(jsonElement, "behaviour trade");
            LootPoolEntry costA = JsonHelper.deserialize(jsonObject, "cost_a", context, LootPoolEntry.class);
            LootPoolEntry costB = JsonHelper.deserialize(jsonObject, "cost_b", EmptyEntry.builder().build(), context, LootPoolEntry.class);
            LootPoolEntry result = JsonHelper.deserialize(jsonObject, "result", context, LootPoolEntry.class);
            LootNumberProvider priceMultiplier = JsonHelper.deserialize(jsonObject, "price_multiplier", ConstantLootNumberProvider.create(0.2f), context, LootNumberProvider.class);
            LootNumberProvider traderExperience = JsonHelper.deserialize(jsonObject, "trader_experience", ConstantLootNumberProvider.create(0), context, LootNumberProvider.class);
            LootNumberProvider maxUses = JsonHelper.deserialize(jsonObject, "max_uses", ConstantLootNumberProvider.create(12), context, LootNumberProvider.class);
            Map<String, LootNumberProvider> referenceProviders = Collections.emptyMap();
            if (jsonObject.has("reference_providers")) {
                referenceProviders = context.deserialize(jsonObject.get("reference_providers"), REFERENCE_PROVIDERS_TYPE);
            }
            boolean rewardExperience = JsonHelper.getBoolean(jsonObject, "reward_experience", true);
            return new BehaviorTrade(costA, costB, result, priceMultiplier, traderExperience, maxUses, referenceProviders, rewardExperience);
        }

        @Override
        public JsonElement serialize(BehaviorTrade behaviorTrade, Type typeOfSrc, JsonSerializationContext context) {
            JsonObject jsonObject = new JsonObject();
            jsonObject.add("cost_a", context.serialize(behaviorTrade.costA));
            if (!(behaviorTrade.costB instanceof EmptyEntry)) {
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
