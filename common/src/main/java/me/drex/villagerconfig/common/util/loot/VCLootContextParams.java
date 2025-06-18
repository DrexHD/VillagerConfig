package me.drex.villagerconfig.common.util.loot;

import me.drex.villagerconfig.common.mixin.loot.LootContextParamSetsAccessor;
import net.minecraft.resources.ResourceLocation;
//? if >= 1.21.2 {
import net.minecraft.util.context.ContextKey;
import net.minecraft.util.context.ContextKeySet;
//?} else {
/*import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
*///?}
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;

import java.util.Map;

import static me.drex.villagerconfig.common.VillagerConfig.MOD_ID;

public class VCLootContextParams {
    public static final /*? if >= 1.21.2 {*/ ContextKey /*?} else {*/ /*LootContextParam *//*?}*/<Map<String, Float>> NUMBER_REFERENCE = create("number_reference");
    public static final /*? if >= 1.21.2 {*/ ContextKeySet /*?} else {*/ /*LootContextParamSet *//*?}*/ VILLAGER_LOOT_CONTEXT = LootContextParamSetsAccessor.invokeRegister("villager", builder -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY).required(NUMBER_REFERENCE));

    public static void init() {
    }

    private static <T> /*? if >= 1.21.2 {*/ ContextKey /*?} else {*/ /*LootContextParam *//*?}*/<T> create(String string) {
        return new /*? if >= 1.21.2 {*/ ContextKey /*?} else {*/ /*LootContextParam *//*?}*/<>(ResourceLocation.fromNamespaceAndPath(MOD_ID, string));
    }
}
