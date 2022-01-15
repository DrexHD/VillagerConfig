package me.drex.villagerfix.json;

import com.google.gson.GsonBuilder;
import me.drex.villagerfix.json.adapter.EnumTypeAdapterFactory;
import me.drex.villagerfix.json.adapter.ItemConvertibleTypeAdapter;
import me.drex.villagerfix.json.adapter.ItemStackTypeAdapter;
import me.drex.villagerfix.json.adapter.RegistryTypeAdapter;
import me.drex.villagerfix.util.Deobfuscator;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootPool;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.entry.LootPoolEntryTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.operator.BoundedIntUnaryOperator;
import net.minecraft.loot.provider.nbt.LootNbtProvider;
import net.minecraft.loot.provider.nbt.LootNbtProviderTypes;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.loot.provider.score.LootScoreProvider;
import net.minecraft.loot.provider.score.LootScoreProviderTypes;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerType;
import net.minecraft.world.gen.feature.StructureFeature;

public class TradeGsons {

    public static GsonBuilder getTradeGsonBuilder() {
        final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        customTypeAdapters(gsonBuilder);
        return gsonBuilder;
    }

    private static void customTypeAdapters(GsonBuilder gsonBuilder) {
        // Field name conversion
        gsonBuilder.setFieldNamingStrategy(f -> Deobfuscator.deobfuscate(f.getName()));
        // Custom type adapters
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(ItemConvertible.class, new ItemConvertibleTypeAdapter());
        // Registry type adapters
        gsonBuilder.registerTypeHierarchyAdapter(StructureFeature.class, new RegistryTypeAdapter<>(Registry.STRUCTURE_FEATURE));
        gsonBuilder.registerTypeHierarchyAdapter(StatusEffect.class, new RegistryTypeAdapter<>(Registry.STATUS_EFFECT));
        gsonBuilder.registerTypeHierarchyAdapter(Enchantment.class, new RegistryTypeAdapter<>(Registry.ENCHANTMENT));
        gsonBuilder.registerTypeHierarchyAdapter(VillagerType.class, new RegistryTypeAdapter<>(Registry.VILLAGER_TYPE));
        // Custom Factories
        gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
        // Loot table adapters
        gsonBuilder.registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer()).registerTypeHierarchyAdapter(LootNumberProvider.class, LootNumberProviderTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootCondition.class, LootConditionTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootScoreProvider.class, LootScoreProviderTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
        gsonBuilder.registerTypeHierarchyAdapter(LootPoolEntry.class, LootPoolEntryTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootFunction.class, LootFunctionTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootNbtProvider.class, LootNbtProviderTypes.createGsonSerializer());
        gsonBuilder.registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer());
    }

}
