package me.drex.villagerconfig.util.loot.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class MultiplyLootNumberProvider implements LootNumberProvider {

    private final LootNumberProvider[] factors;

    MultiplyLootNumberProvider(LootNumberProvider... factors) {
        this.factors = factors;
    }

    @Override
    public float nextFloat(LootContext context) {
        float result = 1;
        for (LootNumberProvider addend : factors) {
            result *= addend.nextFloat(context);
        }
        return result;
    }

    @Override
    public LootNumberProviderType getType() {
        return LootNumberProviderTypes.MUL;
    }

    public static MultiplyLootNumberProvider create(LootNumberProvider... factors) {
        return new MultiplyLootNumberProvider(factors);
    }

    public static class Serializer implements JsonSerializer<MultiplyLootNumberProvider> {
        public Serializer() {
        }

        public void toJson(JsonObject jsonObject, MultiplyLootNumberProvider multiplyLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("factors", jsonSerializationContext.serialize(multiplyLootNumberProvider.factors));
        }

        public MultiplyLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootNumberProvider[] factors = JsonHelper.deserialize(jsonObject, "factors", jsonDeserializationContext, LootNumberProvider[].class);
            return new MultiplyLootNumberProvider(factors);
        }
    }
}
