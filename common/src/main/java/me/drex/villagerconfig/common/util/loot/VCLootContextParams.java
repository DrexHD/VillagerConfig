package me.drex.villagerconfig.common.util.loot;

import me.drex.villagerconfig.common.mixin.loot.LootContextParamSetsAccessor;
import net.minecraft.resources.Identifier;
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Map;

import static me.drex.villagerconfig.common.VillagerConfig.MOD_ID;

public class VCLootContextParams {
    public static final ContextKey<Map<String, Float>> NUMBER_REFERENCE = create("number_reference");
    public static final ContextKeySet VILLAGER_LOOT_CONTEXT = LootContextParamSetsAccessor.invokeRegister("villager", builder -> builder
        .required(LootContextParams.ORIGIN)
        .required(LootContextParams.THIS_ENTITY)
        .required(LootContextParams.ADDITIONAL_COST_COMPONENT_ALLOWED)
        .required(NUMBER_REFERENCE)
    );

    public static void init() {
    }

    private static <T> ContextKey<T> create(String string) {
        return new ContextKey<>(Identifier.fromNamespaceAndPath(MOD_ID, string));
    }
}
