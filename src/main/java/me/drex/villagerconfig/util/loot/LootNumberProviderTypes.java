package me.drex.villagerconfig.util.loot;

import me.drex.villagerconfig.mixin.loot.LootNumberProviderTypesAccessor;
import me.drex.villagerconfig.util.loot.number.AddLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.MultiplyLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.ReferenceLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;

import static me.drex.villagerconfig.VillagerConfig.modId;

public class LootNumberProviderTypes {

    public static final LootNumberProviderType REFERENCE = LootNumberProviderTypesAccessor.invokeRegister(modId("reference"), new ReferenceLootNumberProvider.Serializer());
    public static final LootNumberProviderType ADD = LootNumberProviderTypesAccessor.invokeRegister(modId("add"), new AddLootNumberProvider.Serializer());
    public static final LootNumberProviderType MUL = LootNumberProviderTypesAccessor.invokeRegister(modId("multiply"), new MultiplyLootNumberProvider.Serializer());

    public static void init() {
    }

}
