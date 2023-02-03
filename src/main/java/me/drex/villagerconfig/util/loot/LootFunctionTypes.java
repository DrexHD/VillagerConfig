package me.drex.villagerconfig.util.loot;

import me.drex.villagerconfig.mixin.loot.LootFunctionTypesAccessor;
import me.drex.villagerconfig.util.loot.function.SetDyeFunction;
import me.drex.villagerconfig.util.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.loot.function.LootFunctionType;

import static me.drex.villagerconfig.VillagerConfig.modId;

public class LootFunctionTypes {

    public static final LootFunctionType SET_DYE = LootFunctionTypesAccessor.invokeRegister(modId("set_dye"), new SetDyeFunction.Serializer());
    public static final LootFunctionType ENCHANT_RANDOMLY = LootFunctionTypesAccessor.invokeRegister(modId("enchant_randomly"), new EnchantRandomlyLootFunction.Serializer());

    public static void init() {
    }

}
