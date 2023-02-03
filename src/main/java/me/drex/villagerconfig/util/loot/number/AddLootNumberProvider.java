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

public class AddLootNumberProvider implements LootNumberProvider {

    private final LootNumberProvider[] addends;

    AddLootNumberProvider(LootNumberProvider... addends) {
        this.addends = addends;
    }

    @Override
    public float nextFloat(LootContext context) {
        float result = 0;
        for (LootNumberProvider addend : addends) {
            result += addend.nextFloat(context);
        }
        return result;
    }

    @Override
    public LootNumberProviderType getType() {
        return LootNumberProviderTypes.ADD;
    }

    public static AddLootNumberProvider create(LootNumberProvider... addends) {
        return new AddLootNumberProvider(addends);
    }

    public static class Serializer implements JsonSerializer<AddLootNumberProvider> {
        public Serializer() {
        }

        public void toJson(JsonObject jsonObject, AddLootNumberProvider addLootNumberProvider, JsonSerializationContext jsonSerializationContext) {
            jsonObject.add("addends", jsonSerializationContext.serialize(addLootNumberProvider.addends));
        }

        public AddLootNumberProvider fromJson(JsonObject jsonObject, JsonDeserializationContext jsonDeserializationContext) {
            LootNumberProvider[] addends = JsonHelper.deserialize(jsonObject, "addends", jsonDeserializationContext, LootNumberProvider[].class);
            return new AddLootNumberProvider(addends);
        }
    }
}
