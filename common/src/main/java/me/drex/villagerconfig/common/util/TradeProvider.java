package me.drex.villagerconfig.common.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerconfig.common.data.BehaviorTrade;
import me.drex.villagerconfig.common.data.TradeGroup;
import me.drex.villagerconfig.common.data.TradeTable;
import me.drex.villagerconfig.common.data.TradeTier;
import me.drex.villagerconfig.common.mixin.VillagerDataAccessor;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.npc.villager.VillagerProfession;
import net.minecraft.world.item.trading.TradeCost;
import net.minecraft.world.item.trading.TradeSet;
import net.minecraft.world.item.trading.TradeSets;
import net.minecraft.world.item.trading.VillagerTrade;
import net.minecraft.world.level.storage.loot.entries.LootItem;
//? if >= 26.3 {
import net.minecraft.world.level.storage.loot.entries.UniformContainerBase;
//? } else {
//import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
//? }
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.SetComponentsFunction;
import net.minecraft.world.level.storage.loot.functions.SetItemCountFunction;
//? if >= 26.3 {
import net.minecraft.world.level.storage.loot.providers.number.ints.ContextIntProviders;
//? } else {
//import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
//? }
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;

public class TradeProvider implements DataProvider {

    private final PackOutput.PathProvider pathResolver;
    private final CompletableFuture<HolderLookup.Provider> registries;
    public static final Identifier WANDERING_TRADER_ID = Identifier.withDefaultNamespace("wanderingtrader");

    public TradeProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "trades");
        this.registries = registries;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
        return this.registries.thenCompose(provider -> run(writer, provider));
    }

    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer, HolderLookup.Provider provider) {
        HashMap<Identifier, TradeData> map = Maps.newHashMap();

        // Save all villager trades
        for (Map.Entry<ResourceKey<VillagerProfession>, VillagerProfession> entry : BuiltInRegistries.VILLAGER_PROFESSION.entrySet()) {
            ResourceKey<VillagerProfession> key = entry.getKey();
            VillagerProfession villagerProfession = entry.getValue();
            map.put(key.identifier(), new TradeData(villagerProfession.tradeSetsByLevel(), true));
        }

        // Save wandering trader trades
        Int2ObjectMap<ResourceKey<TradeSet>> trades = Int2ObjectMap.ofEntries(
            Int2ObjectMap.entry(1, TradeSets.WANDERING_TRADER_BUYING),
            Int2ObjectMap.entry(2, TradeSets.WANDERING_TRADER_UNCOMMON),
            Int2ObjectMap.entry(3, TradeSets.WANDERING_TRADER_COMMON)
        );
        map.put(WANDERING_TRADER_ID, new TradeData(trades, false));

        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            Identifier id = entry.getKey();
            TradeData tradeData = entry.getValue();
            Path path = this.pathResolver.json(id);
            int maxLevel = 0;
            while (tradeData.trades().containsKey(maxLevel + 1)) {
                maxLevel++;
            }

            TradeGroup[] tradeGroups = new TradeGroup[maxLevel];
            tradeData.trades().forEach((level, tradeSetKey) -> {
                if (level <= 0 || level > tradeGroups.length) {
                    LOGGER.warn("Invalid trade level {}, expected 1 - {}, for villager type {}", level, tradeGroups.length, id);
                    return;
                }
                TradeSet tradeSet = provider.getOrThrow(tradeSetKey).value();
                //? if >= 26.3 {
                TradeGroup tradeGroup = new TradeGroup(tradeSet.amount(), tradeSet.trades().stream().map(itemListing -> convert(itemListing.value())).map(BehaviorTrade.Builder::build).toList());
                //? } else {
                //TradeGroup tradeGroup = new TradeGroup(tradeSet.amount, tradeSet.getTrades().stream().map(itemListing -> convert(itemListing.value())).map(BehaviorTrade.Builder::build).toList());
                //? }
                tradeGroups[level - 1] = tradeGroup;
            });
            final TradeTier[] tiers;
            if (tradeData.useTiers()) {
                tiers = new TradeTier[maxLevel];
                for (int i = 0; i < tradeGroups.length; i++) {
                    tiers[i] = new TradeTier((VillagerDataAccessor.getNextLevelXpThresholds()[i]), List.of(tradeGroups[i]));
                }
            } else {
                tiers = new TradeTier[]{
                    new TradeTier(0, List.of(tradeGroups))
                };
            }
            TradeTable tradeTable = new TradeTable(List.of(tiers));
            return DataProvider.saveStable(writer, provider, TradeTable.CODEC, tradeTable, path);
        }).toArray(CompletableFuture[]::new));
    }

    private BehaviorTrade.Builder convert(VillagerTrade villagerTrade) {
        //? if >= 26.3 {
        UniformContainerBase.Builder<?> costA = convertCost(villagerTrade.wants);
        //? } else {
        //LootPoolSingletonContainer.Builder<?> costA = convertCost(villagerTrade.wants);
        //? }

        //? if >= 26.3 {
        UniformContainerBase.Builder<?> result = LootItem.lootTableItem(villagerTrade.gives.item().value())
        //? } else {
        //LootPoolSingletonContainer.Builder<?> result = LootItem.lootTableItem(villagerTrade.gives.item().value())
        //? }
            //? if >= 26.3 {
            .apply(SetItemCountFunction.setCount(ContextIntProviders.exactly(villagerTrade.gives.count())));
            //? } else {
            //.apply(SetItemCountFunction.setCount(ConstantValue.exactly(villagerTrade.gives.count())));
            //? }
        //? if >= 26.3 {
        var resultComponents = villagerTrade.gives.components().split();
        if (!resultComponents.removed().isEmpty()) throw new IllegalStateException("Component value is empty");
        for (var component : resultComponents.added()) {
            result.apply(SetComponentsFunction.setComponent((DataComponentType<Object>) component.type(), component.value()));
        }
        //? } else {
        /*
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : villagerTrade.gives.components().entrySet()) {
            Optional<?> value = entry.getValue();
            if (value.isEmpty()) throw new IllegalStateException("Component value is empty");
            result.apply(SetComponentsFunction.setComponent((DataComponentType) entry.getKey(), value.get()));
        }
        */
        //? }
        //? if >= 26.3 {
        villagerTrade.givenItemModifier.ifPresent(result::apply);
        //? } else {
        //for (LootItemFunction givenItemModifier : villagerTrade.givenItemModifiers) {
        //    result.functions.add(givenItemModifier);
        //}
        //? }

        BehaviorTrade.Builder tradeBuilder = new BehaviorTrade.Builder(costA, result)
            .maxUses(villagerTrade.maxUses)
            .priceMultiplier(villagerTrade.reputationDiscount)
            .traderExperience(villagerTrade.xp);
        villagerTrade.merchantPredicate.ifPresent(tradeBuilder::when);

        villagerTrade.doubleTradePriceEnchantments.ifPresent(tradeBuilder::doubleTradePriceEnchantments);
        villagerTrade.additionalWants.map(this::convertCost).ifPresent(lootPool -> tradeBuilder.costB(lootPool.build()));

        return tradeBuilder;
    }

    //? if >= 26.3 {
    private UniformContainerBase.Builder<?> convertCost(TradeCost tradeCost) {
    //? } else {
    //private LootPoolSingletonContainer.Builder<?> convertCost(TradeCost tradeCost) {
    //? }
        //? if >= 26.3 {
        UniformContainerBase.Builder<?> costA = LootItem.lootTableItem(tradeCost.item().value())
        //? } else {
        //LootPoolSingletonContainer.Builder<?> costA = LootItem.lootTableItem(tradeCost.item().value())
        //? }
            .apply(SetItemCountFunction.setCount(tradeCost.count()));
        //? if >= 26.3 {
        var costComponents = tradeCost.components().asPatch().split();
        if (!costComponents.removed().isEmpty()) throw new IllegalStateException("Expected component is empty");
        for (var component : costComponents.added()) {
            costA.apply(SetComponentsFunction.setComponent((DataComponentType<Object>) component.type(), component.value()));
        }
        //? } else {
        /*
        for (Map.Entry<DataComponentType<?>, Optional<?>> entry : tradeCost.components().asPatch().entrySet()) {
            Optional<?> value = entry.getValue();
            if (value.isEmpty()) throw new IllegalStateException("Expected component is empty");
            costA.apply(SetComponentsFunction.setComponent((DataComponentType) entry.getKey(), value.get()));
        }
        */
        //? }
        return costA;
    }

    @Override
    public @NotNull String getName() {
        return "Trades";
    }

    public record TradeData(Int2ObjectMap<ResourceKey<TradeSet>> trades, boolean useTiers) {
    }

}
