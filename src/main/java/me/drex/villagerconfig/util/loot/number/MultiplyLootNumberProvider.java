package me.drex.villagerconfig.util.loot.number;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public record MultiplyLootNumberProvider(List<NumberProvider> factors) implements NumberProvider {

    public static final MapCodec<MultiplyLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        NumberProviders.CODEC.listOf().fieldOf("factors").forGetter(MultiplyLootNumberProvider::factors)
    ).apply(instance, MultiplyLootNumberProvider::new));

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
        return new MultiplyLootNumberProvider(List.of(factors));
    }

}
