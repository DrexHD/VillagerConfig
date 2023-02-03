package me.drex.villagerconfig.util.loot;

import me.drex.villagerconfig.mixin.loot.LootContextParametersAccessor;
import me.drex.villagerconfig.mixin.loot.LootContextTypesAccessor;
import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextType;

import java.util.Map;

import static me.drex.villagerconfig.VillagerConfig.modId;

public class LootContextTypes {

    public static final LootContextParameter<Map<String, Float>> NUMBER_REFERENCE = LootContextParametersAccessor.invokeRegister(modId("number_reference"));
    public static final LootContextType VILLAGER_LOOT_CONTEXT = LootContextTypesAccessor.invokeRegister("villager", builder -> builder.require(LootContextParameters.THIS_ENTITY).require(NUMBER_REFERENCE));

    public static void init() {
    }

}
