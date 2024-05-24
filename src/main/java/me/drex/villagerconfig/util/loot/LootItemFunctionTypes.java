package me.drex.villagerconfig.util.loot;

import com.mojang.serialization.MapCodec;
import me.drex.villagerconfig.util.loot.function.EnchantRandomlyLootFunction;
import me.drex.villagerconfig.util.loot.function.SetDyeFunction;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;

import static me.drex.villagerconfig.VillagerConfig.MOD_ID;

public class LootItemFunctionTypes {

    public static final LootItemFunctionType<SetDyeFunction> SET_DYE = register("set_dye", SetDyeFunction.CODEC);
    public static final LootItemFunctionType<EnchantRandomlyLootFunction> ENCHANT_RANDOMLY = register("enchant_randomly", EnchantRandomlyLootFunction.CODEC);

    public static void init() {
    }

    private static <T extends LootItemFunction> LootItemFunctionType<T> register(String string, MapCodec<T> mapCodec) {
        return Registry.register(BuiltInRegistries.LOOT_FUNCTION_TYPE, ResourceLocation.fromNamespaceAndPath(MOD_ID, string), new LootItemFunctionType<>(mapCodec));
    }

}
