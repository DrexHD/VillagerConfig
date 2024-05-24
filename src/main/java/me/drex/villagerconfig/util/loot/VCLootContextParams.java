package me.drex.villagerconfig.util.loot;

import me.drex.villagerconfig.mixin.loot.LootContextParamSetsAccessor;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Map;

import static me.drex.villagerconfig.VillagerConfig.MOD_ID;

public class VCLootContextParams {
    public static final LootContextParam<Map<String, Float>> NUMBER_REFERENCE = create("number_reference");
    public static final LootContextParamSet VILLAGER_LOOT_CONTEXT = LootContextParamSetsAccessor.invokeRegister("villager", builder -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(NUMBER_REFERENCE));

    public static void init() {
    }

    private static <T> LootContextParam<T> create(String string) {
        return new LootContextParam<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, string));
    }
}
