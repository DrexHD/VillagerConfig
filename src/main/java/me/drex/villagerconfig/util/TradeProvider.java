package me.drex.villagerconfig.util;

import com.google.common.collect.Maps;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import me.drex.villagerconfig.data.BehaviorTrade;
import me.drex.villagerconfig.data.TradeGroup;
import me.drex.villagerconfig.data.TradeTable;
import me.drex.villagerconfig.data.TradeTier;
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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionBrewing;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.SuspiciousEffectHolder;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.*;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

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
            int levels = tradeData.trades().size();
            final TradeTier[] tiers = new TradeTier[levels];
            tradeData.trades().forEach((level, factoryArr) -> {
                TradeGroup tradeGroup = new TradeGroup(ConstantValue.exactly(tradeData.offerCountType().getOfferCount(level)), Arrays.stream(factoryArr).map(this::convert).flatMap(Stream::of).map(BehaviorTrade.Builder::build).filter(Objects::nonNull).toList());
                tiers[level - 1] = new TradeTier((VillagerDataAccessor.getNextLevelXpThresholds()[level - 1]), List.of(tradeGroup));
            });
            TradeTable tradeTable = new TradeTable(List.of(tiers));
            return DataProvider.saveStable(writer, TradeTable.CODEC, tradeTable, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Trades";
    }

    private BehaviorTrade.Builder[] convert(VillagerTrades.ItemListing original) {
        if (original instanceof VillagerTrades.EmeraldForItems factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(factory.itemStack.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.itemStack.getCount()))),
                LootItem.lootTableItem(Items.EMERALD)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.ItemsForEmeralds factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                LootItem.lootTableItem(factory.itemStack.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.itemStack.getCount())))
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.SuspiciousStewForEmerald factory) {
            LootPoolSingletonContainer.Builder<?> suspciousStewBuilder = LootItem.lootTableItem(Items.SUSPICIOUS_STEW);
            for (SuspiciousEffectHolder.EffectEntry effect : factory.effects) {
                suspciousStewBuilder.apply(new SetStewEffectFunction.Builder().withEffect(effect.effect(), ConstantValue.exactly(effect.duration())));
            }
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD),
                suspciousStewBuilder
            ).traderExperience(factory.xp)};
        } else if (original instanceof VillagerTrades.ItemsAndEmeraldsToItems factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                LootItem.lootTableItem(factory.fromItem.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.fromItem.getCount()))),
                LootItem.lootTableItem(factory.toItem.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.toItem.getCount())))
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.EnchantedItemForEmeralds factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.baseEmeraldCost)))
                    .apply(SetItemCountFunction.setCount(ReferenceLootNumberProvider.create("enchantLevel"), true)),
                LootItem.lootTableItem(factory.itemStack.getItem()).apply(new EnchantWithLevelsFunction.Builder(ReferenceLootNumberProvider.create("enchantLevel")))
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses).numberReference("enchantLevel", UniformGenerator.between(5, 19))};
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
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                AlternativesEntry.alternatives(children),
                LootItem.lootTableItem(Items.EMERALD)
            ).priceMultiplier(0.05f).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.TippedArrowForItemsAndEmeralds factory) {
            List<Potion> potions = BuiltInRegistries.POTION.stream().filter(potion -> !potion.getEffects().isEmpty() && PotionBrewing.isBrewablePotion(potion)).toList();
            LootPoolEntryContainer.Builder<?>[] entries = new LootPoolEntryContainer.Builder[potions.size()];
            for (int i = 0; i < potions.size(); i++) {
                Potion potion = potions.get(i);
                entries[i] = LootItem.lootTableItem(factory.toItem.getItem()).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.toCount))).apply(SetPotionFunction.setPotion(potion));
            }
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                LootItem.lootTableItem(factory.fromItem).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.fromCount))),
                EntryGroup.list(entries)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.EnchantBookForEmeralds factory) {
            List<Enchantment> defaultEnchantments = BuiltInRegistries.ENCHANTMENT.stream().filter(Enchantment::isTradeable).toList();
            EnchantRandomlyLootFunction.Builder enchantRandomlyFunction = new EnchantRandomlyLootFunction.Builder()
                .minLevel(factory.minLevel).maxLevel(factory.maxLevel)
                .tradeEnchantments();
            if (!defaultEnchantments.equals(factory.tradeableEnchantments)) {
                System.out.println(defaultEnchantments);
                System.out.println(factory.tradeableEnchantments);
                enchantRandomlyFunction.include(factory.tradeableEnchantments.toArray(Enchantment[]::new));
            }
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
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
                LootItem.lootTableItem(Items.BOOK).apply(enchantRandomlyFunction)
            ).traderExperience(factory.villagerXp)};
        } else if (original instanceof VillagerTrades.TreasureMapForEmeralds factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                LootItem.lootTableItem(Items.COMPASS),
                LootItem.lootTableItem(Items.MAP)
                    .apply(new ExplorationMapFunction.Builder().setSearchRadius(100).setMapDecoration(factory.destinationType).setDestination(factory.destination).setZoom((byte) 2).setSkipKnownStructures(true))
                    .apply(SetNameFunction.setName(Component.translatable(factory.displayName)))
            ).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.DyedArmorForEmeralds factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.value))),
                LootItem.lootTableItem(factory.item),
                LootItem.lootTableItem(factory.item)
                    .apply(new SetDyeFunction.Builder(true))
                    .apply(new SetDyeFunction.Builder(true).when(LootItemRandomChanceCondition.randomChance(0.3f)))
                    .apply(new SetDyeFunction.Builder(true).when(LootItemRandomChanceCondition.randomChance(0.2f)))

            ).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.TypeSpecificTrade factory) {
            List<BehaviorTrade.Builder> trades = new ArrayList<>(BuiltInRegistries.VILLAGER_TYPE.size());
            for (Map.Entry<VillagerType, VillagerTrades.ItemListing> entry : factory.trades().entrySet()) {
                CompoundTag root = new CompoundTag();
                CompoundTag villagerData = new CompoundTag();
                villagerData.putString("type", BuiltInRegistries.VILLAGER_TYPE.getKey(entry.getKey()).toString());
                root.put("VillagerData", villagerData);
                for (BehaviorTrade.Builder behaviorTrade : convert(entry.getValue())) {
                    behaviorTrade.when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().nbt(new NbtPredicate(root))));
                    trades.add(behaviorTrade);
                }
            }
            return trades.toArray(BehaviorTrade.Builder[]::new);
        }
        LOGGER.warn("Unable to convert {}, generated json won't be complete!", original.getClass());
        return new BehaviorTrade.Builder[]{};
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
