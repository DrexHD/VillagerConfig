package me.drex.villagerconfig.common.util.loot;

import com.mojang.serialization.MapCodec;
import me.drex.villagerconfig.common.util.loot.number.AddLootNumberProvider;
import me.drex.villagerconfig.common.util.loot.number.MultiplyLootNumberProvider;
import me.drex.villagerconfig.common.util.loot.number.ReferenceLootNumberProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import static me.drex.villagerconfig.common.VillagerConfig.MOD_ID;

public class LootNumberProviderTypes {

    public static final LootNumberProviderType REFERENCE = register("reference", ReferenceLootNumberProvider.CODEC);
    public static final LootNumberProviderType ADD = register("add", AddLootNumberProvider.CODEC);
    public static final LootNumberProviderType MUL = register("multiply", MultiplyLootNumberProvider.CODEC);

    public static void init() {
    }

    private static LootNumberProviderType register(String string, MapCodec<? extends NumberProvider> mapCodec) {
        return Registry.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, string), new LootNumberProviderType(mapCodec));
    }

}
