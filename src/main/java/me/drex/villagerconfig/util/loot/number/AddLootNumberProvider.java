package me.drex.villagerconfig.util.loot.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record AddLootNumberProvider(List<NumberProvider> addends) implements NumberProvider {

    public static final Codec<AddLootNumberProvider> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        NumberProviders.CODEC.listOf().fieldOf("addends").forGetter(AddLootNumberProvider::addends)
    ).apply(instance, AddLootNumberProvider::new));

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
        return new AddLootNumberProvider(List.of(addends));
    }

}
