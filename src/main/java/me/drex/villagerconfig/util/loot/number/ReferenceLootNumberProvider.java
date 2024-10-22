package me.drex.villagerconfig.util.loot.number;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import me.drex.villagerconfig.util.loot.VCLootContextParams;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import org.jetbrains.annotations.NotNull;

public record ReferenceLootNumberProvider(String id) implements NumberProvider {

    public static final MapCodec<ReferenceLootNumberProvider> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
        Codec.STRING.fieldOf("id").forGetter(ReferenceLootNumberProvider::id)
    ).apply(instance, ReferenceLootNumberProvider::new));

    @Override
    public float getFloat(LootContext context) {
        if (!context.hasParameter(VCLootContextParams.NUMBER_REFERENCE)) {
            return 0f;
        }
        return context.getParameter(VCLootContextParams.NUMBER_REFERENCE).getOrDefault(id, 0f);
    }

    @Override
    public @NotNull LootNumberProviderType getType() {
        return LootNumberProviderTypes.REFERENCE;
    }

    public static ReferenceLootNumberProvider create(String id) {
        return new ReferenceLootNumberProvider(id);
    }

}
