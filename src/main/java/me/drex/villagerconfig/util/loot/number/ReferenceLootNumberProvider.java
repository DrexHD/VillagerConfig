package me.drex.villagerconfig.util.loot.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.drex.villagerconfig.util.loot.LootContextTypes;
import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.util.JsonHelper;
import net.minecraft.util.JsonSerializer;

public class ReferenceLootNumberProvider implements LootNumberProvider {

    private final String id;

    ReferenceLootNumberProvider(String id) {
        this.id = id;
    }

    @Override
    public float nextFloat(LootContext context) {
        if (!context.hasParameter(LootContextTypes.NUMBER_REFERENCE)) {
            return 0f;
        }
        return context.get(LootContextTypes.NUMBER_REFERENCE).getOrDefault(id, 0f);
    }

    @Override
    public LootNumberProviderType getType() {
        return LootNumberProviderTypes.REFERENCE;
    }

    public static ReferenceLootNumberProvider create(String id) {
        return new ReferenceLootNumberProvider(id);
    }

    public static class Serializer implements JsonSerializer<ReferenceLootNumberProvider> {
        public Serializer() {
        }

        public void toJson(JsonObject jsonObject, ReferenceLootNumberProvider referenceLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.addProperty("id", referenceLootNumberProvider.id);
        }

        public ReferenceLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            String id = JsonHelper.getString(jsonObject, "id");
            return new ReferenceLootNumberProvider(id);
        }
    }
}
