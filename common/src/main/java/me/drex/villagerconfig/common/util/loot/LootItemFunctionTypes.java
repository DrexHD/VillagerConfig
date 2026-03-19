package me.drex.villagerconfig.common.util.loot;

import com.mojang.serialization.MapCodec;
import me.drex.villagerconfig.common.util.loot.function.EnchantRandomlyLootFunction;
import me.drex.villagerconfig.common.util.loot.function.SetDyeFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;

import static me.drex.villagerconfig.common.VillagerConfig.MOD_ID;

public class LootItemFunctionTypes {

    public static void init() {
        register("set_dye", SetDyeFunction.CODEC);
        register("enchant_randomly", EnchantRandomlyLootFunction.CODEC);
    }

    private static MapCodec<? extends LootItemFunction> register(String string, MapCodec<? extends LootItemFunction> mapCodec) {
        return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, Identifier.fromNamespaceAndPath(MOD_ID, string), mapCodec);
    }

}
