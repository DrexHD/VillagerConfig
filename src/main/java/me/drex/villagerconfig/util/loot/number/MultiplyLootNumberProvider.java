package me.drex.villagerconfig.util.loot.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public class MultiplyLootNumberProvider implements NumberProvider {

    private final NumberProvider[] factors;

    MultiplyLootNumberProvider(NumberProvider... factors) {
        this.factors = factors;
    }

    @Override
    public float getFloat(@NotNull LootContext context) {
        float result = 1;
        for (NumberProvider addend : factors) {
            result *= addend.getFloat(context);
        }
        return result;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return LootNumberProviderTypes.MUL;
    }

    public static MultiplyLootNumberProvider create(NumberProvider... factors) {
        return new MultiplyLootNumberProvider(factors);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<MultiplyLootNumberProvider> {
        public Serializer() {
        }

        @Override
        public void serialize(JsonObject jsonObject, MultiplyLootNumberProvider multiplyLootNumberProvider, JsonSerializationContext context) {
            jsonObject.add("factors", context.serialize(multiplyLootNumberProvider.factors));
        }

        public @NotNull MultiplyLootNumberProvider deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext jsonDeserializationContext) {
            NumberProvider[] factors = GsonHelper.getAsObject(jsonObject, "factors", jsonDeserializationContext, NumberProvider[].class);
            return new MultiplyLootNumberProvider(factors);
        }
    }
}
