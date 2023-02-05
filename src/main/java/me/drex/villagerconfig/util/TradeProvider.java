package me.drex.villagerconfig.util;

import com.google.common.collect.Maps;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import me.drex.villagerconfig.json.TradeGsons;
import me.drex.villagerconfig.json.data.BehaviorTrade;
import me.drex.villagerconfig.json.data.TradeGroup;
import me.drex.villagerconfig.json.data.TradeTable;
import me.drex.villagerconfig.json.data.TradeTier;
import me.drex.villagerconfig.mixin.VillagerDataAccessor;
import me.drex.villagerconfig.util.loot.function.EnchantRandomlyLootFunction;
import me.drex.villagerconfig.util.loot.function.SetDyeFunction;
import me.drex.villagerconfig.util.loot.number.AddLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.MultiplyLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.ReferenceLootNumberProvider;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.advancements.critereon.NbtPredicate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.AlternativesEntry;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.VILLAGER;
import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.WANDERING_TRADER;

public class TradeProvider implements DataProvider {

    private final PackOutput.PathProvider pathResolver;
    public static final ResourceLocation WANDERING_TRADER_ID = new ResourceLocation("wanderingtrader");
    private static final IntUnaryOperator WANDERING_TRADER_COUNT = i -> switch (i) {
        case 1 -> 5;
        case 2 -> 1;
        default -> 0;
    };

    public TradeProvider(PackOutput output) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "trades");
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
        HashMap<ResourceLocation, TradeData> map = Maps.newHashMap();

        // Save all villager trades
        for (VillagerProfession villagerProfession : BuiltInRegistries.VILLAGER_PROFESSION) {
            map.put(BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerProfession), new TradeData(VillagerTrades.TRADES.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>()), VILLAGER));
        }
        // Save wandering trader trades
        map.put(WANDERING_TRADER_ID, new TradeData(VillagerTrades.WANDERING_TRADER_TRADES, WANDERING_TRADER));
        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            ResourceLocation identifier = entry.getKey();
            TradeData tradeData = entry.getValue();
            Path path = this.pathResolver.json(identifier);
            return DataProvider.saveStable(writer, toJson(tradeData), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Trades";
    }

    private JsonElement toJson(TradeData tradeData) {
        int levels = tradeData.trades().size();
        final TradeTier[] tiers = new TradeTier[levels];
        tradeData.trades().forEach((level, factoryArr) -> {
            TradeGroup tradeGroup = new TradeGroup(tradeData.offerCountType().getOfferCount(level), Arrays.stream(factoryArr).map(this::convert).filter(Objects::nonNull).toArray(BehaviorTrade[]::new));
            tiers[level - 1] = new TradeTier((VillagerDataAccessor.getNextLevelXpThresholds()[level - 1]), new TradeGroup[]{tradeGroup});
        });
        TradeTable tradeTable = new TradeTable(tiers);
        return TradeGsons.GSON.toJsonTree(tradeTable);
    }

    private BehaviorTrade convert(VillagerTrades.ItemListing original) {
        if (original instanceof VillagerTrades.EmeraldForItems factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(factory.item).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.cost))),
                    LootItem.lootTableItem(Items.EMERALD)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses).build();
        } else if (original instanceof VillagerTrades.ItemsForEmeralds factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                    LootItem.lootTableItem(factory.itemStack.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.numberOfItems)))
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses).build();
        } else if (original instanceof VillagerTrades.SuspiciousStewForEmerald factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.EMERALD),
                    LootItem.lootTableItem(Items.SUSPICIOUS_STEW).apply(new SetStewEffectFunction.Builder().withEffect(factory.effect, ConstantValue.exactly(factory.duration)))
            ).traderExperience(factory.xp).build();
        } else if (original instanceof VillagerTrades.ItemsAndEmeraldsToItems factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                    LootItem.lootTableItem(factory.fromItem.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.fromCount))),
                    LootItem.lootTableItem(factory.toItem.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.toCount)))
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses).build();
        } else if (original instanceof VillagerTrades.EnchantedItemForEmeralds factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.EMERALD)
                            .apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.baseEmeraldCost)))
                            .apply(SetItemCountFunction.setCount(ReferenceLootNumberProvider.create("enchantLevel"), true)),
                    LootItem.lootTableItem(factory.itemStack.getItem()).apply(new EnchantWithLevelsFunction.Builder(ReferenceLootNumberProvider.create("enchantLevel")))
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses).numberReference("enchantLevel", UniformGenerator.between(5, 19)).build();
        } else if (original instanceof VillagerTrades.EmeraldsForVillagerTypeItem factory) {
            LootPoolEntryContainer.Builder<?>[] children = new LootPoolEntryContainer.Builder[BuiltInRegistries.VILLAGER_TYPE.size()];
            int i = 0;
            for (VillagerType villagerType : BuiltInRegistries.VILLAGER_TYPE) {
                CompoundTag root = new CompoundTag();
                CompoundTag villagerData = new CompoundTag();
                villagerData.putString("type", BuiltInRegistries.VILLAGER_TYPE.getKey(villagerType).toString());
                root.put("VillagerData", villagerData);
                children[i] = LootItem.lootTableItem(factory.trades.get(villagerType)).apply(
                        SetItemCountFunction.setCount(ConstantValue.exactly(factory.cost))
                ).when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().nbt(new NbtPredicate(root))));
                i++;
            }
            return new BehaviorTrade.Builder(
                    AlternativesEntry.alternatives(children),
                    LootItem.lootTableItem(Items.EMERALD)
            ).priceMultiplier(0.05f).traderExperience(factory.villagerXp).maxUses(factory.maxUses).build();
        } else if (original instanceof VillagerTrades.TippedArrowForItemsAndEmeralds factory) {
            List<Potion> potions = BuiltInRegistries.POTION.stream().filter(potion -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion)).toList();
            LootPoolEntryContainer.Builder<?>[] entries = new LootPoolEntryContainer.Builder[potions.size()];
            for (int i = 0; i < potions.size(); i++) {
                Potion potion = potions.get(i);
                entries[i] = LootItem.lootTableItem(factory.toItem.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.toCount))).apply(SetPotionFunction.setPotion(potion));
            }
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                    LootItem.lootTableItem(factory.fromItem).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.fromCount))),
                    EntryGroup.list(entries)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses).build();
        } else if (original instanceof VillagerTrades.EnchantBookForEmeralds factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.BOOK),
                    LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(
                            // Count formula: (2 + (random.nextInt(5 + (enchantmentLevel * 10))) + (3 * enchantmentLevel)) * treasureMultiplier
                            // Treasure multiplier
                            MultiplyLootNumberProvider.create(
                                    // 2 + (random.nextInt(5 + (enchantmentLevel * 10))) + (3 * enchantmentLevel)
                                    AddLootNumberProvider.create(
                                            // 2
                                            ConstantValue.exactly(2),
                                            // (random.nextInt(5 + (enchantmentLevel * 10)))
                                            new UniformGenerator(
                                                    ConstantValue.exactly(0),
                                                    // 5 + (enchantmentLevel * 10))
                                                    AddLootNumberProvider.create(
                                                            // 5
                                                            ConstantValue.exactly(5),
                                                            // enchantmentLevel * 10
                                                            MultiplyLootNumberProvider.create(
                                                                    ReferenceLootNumberProvider.create("enchantmentLevel"),
                                                                    ConstantValue.exactly(10)
                                                            )
                                                    )
                                            ),
                                            // (3 * enchantmentLevel)
                                            MultiplyLootNumberProvider.create(
                                                    ConstantValue.exactly(3),
                                                    ReferenceLootNumberProvider.create("enchantmentLevel")
                                            )

                                    ),
                                    // treasureMultiplier
                                    ReferenceLootNumberProvider.create("treasureMultiplier")
                            )

                    )),
                    LootItem.lootTableItem(Items.BOOK).apply(new EnchantRandomlyLootFunction.Builder().tradeEnchantments())
            ).traderExperience(factory.villagerXp).build();
        } else if (original instanceof VillagerTrades.TreasureMapForEmeralds factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                    LootItem.lootTableItem(Items.COMPASS),
                    LootItem.lootTableItem(Items.MAP).apply(new ExplorationMapFunction.Builder().setSearchRadius(100).setMapDecoration(factory.destinationType).setDestination(factory.destination).setZoom((byte) 2).setSkipKnownStructures(true))
            ).traderExperience(factory.villagerXp).maxUses(factory.maxUses).build();
        } else if (original instanceof VillagerTrades.DyedArmorForEmeralds factory) {
            return new BehaviorTrade.Builder(
                    LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.value))),
                    LootItem.lootTableItem(factory.item),
                    LootItem.lootTableItem(factory.item)
                            .apply(new SetDyeFunction.Builder(true))
                            .apply(new SetDyeFunction.Builder(true).when(LootItemRandomChanceCondition.randomChance(0.3f)))
                            .apply(new SetDyeFunction.Builder(true).when(LootItemRandomChanceCondition.randomChance(0.2f)))
                            
            ).traderExperience(factory.villagerXp).maxUses(factory.maxUses).build();
        }
        LOGGER.warn("Unable to convert {}, generated json won't be complete!", original.getClass());
        return null;
    }

    public enum OfferCountType {
        VILLAGER(i -> 2), WANDERING_TRADER(WANDERING_TRADER_COUNT);

        private final IntUnaryOperator operator;

        OfferCountType(IntUnaryOperator operator) {
            this.operator = operator;
        }

        public int getOfferCount(int level) {
            return operator.apply(level);
        }

    }

    public record TradeData(Int2ObjectMap<VillagerTrades.ItemListing[]> trades, OfferCountType offerCountType) {

    }

}
