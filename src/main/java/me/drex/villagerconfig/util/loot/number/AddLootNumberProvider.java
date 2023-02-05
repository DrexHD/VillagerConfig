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

public class AddLootNumberProvider implements NumberProvider {

    private final NumberProvider[] addends;

    AddLootNumberProvider(NumberProvider... addends) {
        this.addends = addends;
    }

    @Override
    public float getFloat(@NotNull LootContext context) {
        float result = 0;
        for (NumberProvider addend : addends) {
            result += addend.getFloat(context);
        }
        return result;
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return LootNumberProviderTypes.ADD;
    }

    public static AddLootNumberProvider create(NumberProvider... addends) {
        return new AddLootNumberProvider(addends);
    }

    public static class Serializer implements net.minecraft.world.level.storage.loot.Serializer<AddLootNumberProvider> {
        public Serializer() {
        }

        @Override
        public void serialize(JsonObject jsonObject, AddLootNumberProvider addLootNumberProvider, JsonSerializationContext context) {
            jsonObject.add("addends", context.serialize(addLootNumberProvider.addends));
        }

        public @NotNull AddLootNumberProvider deserialize(@NotNull JsonObject jsonObject, @NotNull JsonDeserializationContext context) {
            NumberProvider[] addends = GsonHelper.getAsObject(jsonObject, "addends", context, NumberProvider[].class);
            return new AddLootNumberProvider(addends);
        }
    }
}
