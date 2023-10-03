package me.drex.villagerconfig.util.loot;

import me.drex.villagerconfig.mixin.loot.NumberProvidersAccessor;
import me.drex.villagerconfig.util.loot.number.AddLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.MultiplyLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.ReferenceLootNumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;

import static me.drex.villagerconfig.VillagerConfig.modId;

public class LootNumberProviderTypes {

    public static final LootNumberProviderType REFERENCE = NumberProvidersAccessor.invokeRegister(modId("reference"), ReferenceLootNumberProvider.CODEC);
    public static final LootNumberProviderType ADD = NumberProvidersAccessor.invokeRegister(modId("add"), AddLootNumberProvider.CODEC);
    public static final LootNumberProviderType MUL = NumberProvidersAccessor.invokeRegister(modId("multiply"), MultiplyLootNumberProvider.CODEC);

    public static void init() {
    }

}
