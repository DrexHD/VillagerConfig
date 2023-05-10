package me.drex.villagerconfig.util.loot.number;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import me.drex.villagerconfig.util.loot.VCLootContextParams;
import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public class ReferenceLootNumberProvider implements NumberProvider {

    private final String id;

    ReferenceLootNumberProvider(String id) {
        this.id = id;
    }

    @Override
    public float getFloat(LootContext context) {
        if (!context.hasParam(VCLootContextParams.NUMBER_REFERENCE)) {
            return 0f;
        }
        return context.getParamOrNull(VCLootContextParams.NUMBER_REFERENCE).getOrDefault(id, 0f);
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return LootNumberProviderTypes.REFERENCE;
    }

    public static ReferenceLootNumberProvider create(String id) {
        return new ReferenceLootNumberProvider(id);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<ReferenceLootNumberProvider> {
        public Serializer() {
        }

        @Override
        public void serialize(JsonObject jsonObject, ReferenceLootNumberProvider referenceLootNumberProvider, @NotNull JsonSerializationContext context) {
            jsonObject.addProperty("id", referenceLootNumberProvider.id);

        }

        public @NotNull ReferenceLootNumberProvider deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext context) {
            String id = GsonHelper.getAsString(jsonObject, "id");
            return new ReferenceLootNumberProvider(id);
        }
    }
}
