package me.drex.villagerconfig.common.util.loot;

import com.mojang.serialization.MapCodec;
import me.drex.villagerconfig.common.util.loot.number.AddLootNumberProvider;
import me.drex.villagerconfig.common.util.loot.number.MultiplyLootNumberProvider;
import me.drex.villagerconfig.common.util.loot.number.ReferenceLootNumberProvider;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;

import static me.drex.villagerconfig.common.VillagerConfig.MOD_ID;

public class LootNumberProviderTypes {
    public static void init() {
        register("reference", ReferenceLootNumberProvider.CODEC);
        register("add", AddLootNumberProvider.CODEC);
        register("multiply", MultiplyLootNumberProvider.CODEC);
    }

    private static MapCodec<? extends NumberProvider> register(String string, MapCodec<? extends NumberProvider> mapCodec) {
        return Registry.register(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, string), mapCodec);
    }

}
