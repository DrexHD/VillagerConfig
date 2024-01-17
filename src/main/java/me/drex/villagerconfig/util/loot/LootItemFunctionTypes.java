package me.drex.villagerconfig.util.loot;

import me.drex.villagerconfig.mixin.loot.LootItemFunctionsAccessor;
import me.drex.villagerconfig.util.loot.function.EnchantRandomlyLootFunction;
import me.drex.villagerconfig.util.loot.function.SetDyeFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import static me.drex.villagerconfig.VillagerConfig.modId;

public class LootItemFunctionTypes {

    public static final LootItemFunctionType SET_DYE = LootItemFunctionsAccessor.invokeRegister(modId("set_dye"), new SetDyeFunction.Serializer());
    public static final LootItemFunctionType ENCHANT_RANDOMLY = LootItemFunctionsAccessor.invokeRegister(modId("enchant_randomly"), new EnchantRandomlyLootFunction.Serializer());

    public static void init() {
    }

}
