package me.drex.villagerconfig.json;

import com.google.gson.GsonBuilder;
import me.drex.villagerconfig.json.adapter.*;
import me.drex.villagerconfig.util.Deobfuscator;
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
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registries;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerType;

public class TradeGsons {

    public static GsonBuilder getTradeGsonBuilder(DynamicRegistryManager registryManager) {
        final GsonBuilder gsonBuilder = new GsonBuilder();
        customTypeAdapters(gsonBuilder, registryManager);
        return gsonBuilder;
    }

    private static void customTypeAdapters(GsonBuilder gsonBuilder, DynamicRegistryManager registryManager) {
        // Field name conversion
        gsonBuilder.setFieldNamingStrategy(f -> Deobfuscator.deobfuscate(f.getName()));
        // Custom type adapters
        gsonBuilder.registerTypeAdapter(ItemStack.class, new ItemStackTypeAdapter());
        gsonBuilder.registerTypeHierarchyAdapter(ItemConvertible.class, new ItemConvertibleTypeAdapter());
        // Registry type adapters
        gsonBuilder.registerTypeHierarchyAdapter(StatusEffect.class, new RegistryTypeAdapter<>(Registries.STATUS_EFFECT));
        gsonBuilder.registerTypeHierarchyAdapter(Enchantment.class, new RegistryTypeAdapter<>(Registries.ENCHANTMENT));
        gsonBuilder.registerTypeHierarchyAdapter(VillagerType.class, new RegistryTypeAdapter<>(Registries.VILLAGER_TYPE));
        // Custom Factories
        gsonBuilder.registerTypeAdapterFactory(new EnumTypeAdapterFactory());
        gsonBuilder.registerTypeAdapterFactory(new TagKeyTypeAdapterFactory(registryManager));
        gsonBuilder.registerTypeAdapter(TradeOffers.Factory.class, new FactoryTypeAdapter());
        // Loot table adapters
        gsonBuilder.registerTypeAdapter(BoundedIntUnaryOperator.class, new BoundedIntUnaryOperator.Serializer()).registerTypeHierarchyAdapter(LootNumberProvider.class, LootNumberProviderTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootCondition.class, LootConditionTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootScoreProvider.class, LootScoreProviderTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootContext.EntityTarget.class, new LootContext.EntityTarget.Serializer());
        gsonBuilder.registerTypeHierarchyAdapter(LootPoolEntry.class, LootPoolEntryTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootFunction.class, LootFunctionTypes.createGsonSerializer()).registerTypeHierarchyAdapter(LootNbtProvider.class, LootNbtProviderTypes.createGsonSerializer());
        gsonBuilder.registerTypeAdapter(LootPool.class, new LootPool.Serializer()).registerTypeAdapter(LootTable.class, new LootTable.Serializer());
    }

}
