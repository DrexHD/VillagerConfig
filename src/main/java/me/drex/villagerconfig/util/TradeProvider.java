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
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.util.valueproviders.ConstantInt;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.alchemy.PotionContents;
import net.minecraft.world.item.component.SuspiciousStewEffects;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.item.enchantment.providers.EnchantmentProvider;
import net.minecraft.world.item.enchantment.providers.SingleEnchantment;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.EntryGroup;
import net.minecraft.world.level.storage.loot.entries.LootItem;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.functions.*;
import net.minecraft.world.level.storage.loot.predicates.LootItemEntityPropertyCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemRandomChanceCondition;
import net.minecraft.world.level.storage.loot.providers.number.ConstantValue;
import net.minecraft.world.level.storage.loot.providers.number.UniformGenerator;
import org.apache.commons.lang3.tuple.Pair;
import org.jetbrains.annotations.NotNull;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.*;

public class TradeProvider implements DataProvider {

    private final PackOutput.PathProvider pathResolver;
    private final MinecraftServer server;
    private final boolean experimental;
    public static final ResourceLocation WANDERING_TRADER_ID = ResourceLocation.withDefaultNamespace("wanderingtrader");
    private static final IntUnaryOperator WANDERING_TRADER_COUNT = i -> switch (i) {
        case 1 -> 5;
        case 2 -> 1;
        default -> 0;
    };
    private static final IntUnaryOperator EXPERIMENTAL_WANDERING_TRADER_COUNT = i -> {
        if (i > VillagerTrades.EXPERIMENTAL_WANDERING_TRADER_TRADES.size()) {
            // Invalid level
            return 0;
        } else {
            return VillagerTrades.EXPERIMENTAL_WANDERING_TRADER_TRADES.get(i - 1).getValue();
        }
    };

    public TradeProvider(PackOutput output, MinecraftServer server, boolean experimental) {
        this.pathResolver = output.createPathProvider(PackOutput.Target.DATA_PACK, "trades");
        this.server = server;
        this.experimental = experimental;
    }

    @Override
    public @NotNull CompletableFuture<?> run(@NotNull CachedOutput writer) {
        HashMap<ResourceLocation, TradeData> map = Maps.newHashMap();

        // Save all villager trades
        for (VillagerProfession villagerProfession : BuiltInRegistries.VILLAGER_PROFESSION) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> trades = VillagerTrades.TRADES.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>());
            Int2ObjectMap<VillagerTrades.ItemListing[]> experimentalTrades = VillagerTrades.EXPERIMENTAL_TRADES.get(villagerProfession);
            if (experimental && experimentalTrades != null) {
                trades = experimentalTrades;
            }
            map.put(BuiltInRegistries.VILLAGER_PROFESSION.getKey(villagerProfession), new TradeData(trades, VILLAGER));
        }
        // Save wandering trader trades
        if (experimental) {
            Int2ObjectMap<VillagerTrades.ItemListing[]> experimentalTrades = new Int2ObjectArrayMap<>();
            List<Pair<VillagerTrades.ItemListing[], Integer>> experimentalWanderingTraderTrades = VillagerTrades.EXPERIMENTAL_WANDERING_TRADER_TRADES;
            for (int i = 0; i < experimentalWanderingTraderTrades.size(); i++) {
                Pair<VillagerTrades.ItemListing[], Integer> pair = experimentalWanderingTraderTrades.get(i);
                experimentalTrades.put(i + 1, pair.getLeft());
            }
            map.put(WANDERING_TRADER_ID, new TradeData(experimentalTrades, EXPERIMENTAL_WANDERING_TRADER));
        } else {
            map.put(WANDERING_TRADER_ID, new TradeData(VillagerTrades.WANDERING_TRADER_TRADES, WANDERING_TRADER));
        }
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
            return DataProvider.saveStable(writer, server.registryAccess(), TradeTable.CODEC, tradeTable, path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public @NotNull String getName() {
        return "Trades";
    }

    private BehaviorTrade.Builder[] convert(VillagerTrades.ItemListing original) {
        if (original instanceof VillagerTrades.EmeraldForItems factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                lootTableItemStack(factory.itemStack.itemStack()),
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldAmount)))
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.ItemsForEmeralds factory) {
            LootPoolSingletonContainer.Builder<?> result = lootTableItemStack(factory.itemStack);
            enchantItem(result, factory.enchantmentProvider, factory);
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                result
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.SuspiciousStewForEmerald factory) {
            LootPoolSingletonContainer.Builder<?> suspciousStewBuilder = LootItem.lootTableItem(Items.SUSPICIOUS_STEW);
            for (SuspiciousStewEffects.Entry effect : factory.effects.effects()) {
                suspciousStewBuilder.apply(new SetStewEffectFunction.Builder().withEffect(effect.effect(), ConstantValue.exactly(effect.duration())));
            }
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD),
                suspciousStewBuilder
            ).traderExperience(factory.xp)};
        } else if (original instanceof VillagerTrades.ItemsAndEmeraldsToItems factory) {
            LootPoolSingletonContainer.Builder<?> result = lootTableItemStack(factory.fromItem.itemStack());
            enchantItem(result, factory.enchantmentProvider, factory);
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                result,
                lootTableItemStack(factory.toItem)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.EnchantedItemForEmeralds factory) {
            Optional<HolderSet.Named<Enchantment>> optional = server.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getTag(EnchantmentTags.ON_TRADED_EQUIPMENT);
            EnchantWithLevelsFunction.Builder builder = new EnchantWithLevelsFunction.Builder(ReferenceLootNumberProvider.create("enchantLevel"));
            optional.ifPresent(builder::fromOptions);
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD)
                    .apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.baseEmeraldCost)))
                    .apply(SetItemCountFunction.setCount(ReferenceLootNumberProvider.create("enchantLevel"), true)),
                lootTableItemStack(factory.itemStack).apply(builder)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses).numberReference("enchantLevel", UniformGenerator.between(5, 19))};
        } else if (original instanceof VillagerTrades.EmeraldsForVillagerTypeItem factory) {
            List<BehaviorTrade.Builder> trades = new ArrayList<>(factory.trades.size());
            for (Map.Entry<VillagerType, Item> entry : factory.trades.entrySet()) {
                CompoundTag root = new CompoundTag();
                CompoundTag villagerData = new CompoundTag();
                villagerData.putString("type", BuiltInRegistries.VILLAGER_TYPE.getKey(entry.getKey()).toString());
                root.put("VillagerData", villagerData);
                BehaviorTrade.Builder trade = new BehaviorTrade.Builder(
                    LootItem.lootTableItem(factory.trades.get(entry.getKey())).apply(
                        SetItemCountFunction.setCount(ConstantValue.exactly(factory.cost))
                    ),
                    LootItem.lootTableItem(Items.EMERALD)
                ).priceMultiplier(0.05f)
                    .traderExperience(factory.villagerXp).maxUses(factory.maxUses)
                    .when(LootItemEntityPropertyCondition.hasProperties(LootContext.EntityTarget.THIS, EntityPredicate.Builder.entity().nbt(new NbtPredicate(root))));
                trades.add(trade);
            }
            return trades.toArray(BehaviorTrade.Builder[]::new);
        } else if (original instanceof VillagerTrades.TippedArrowForItemsAndEmeralds factory) {
            List<Holder<Potion>> potions = BuiltInRegistries.POTION
                .holders()
                .filter(reference -> !(reference.value()).getEffects().isEmpty() && server.potionBrewing().isBrewablePotion(reference))
                .collect(Collectors.toList());
            LootPoolEntryContainer.Builder<?>[] entries = new LootPoolEntryContainer.Builder[potions.size()];
            for (int i = 0; i < potions.size(); i++) {
                Holder<Potion> potion = potions.get(i);
                entries[i] = lootTableItemStack(factory.toItem).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.toCount))).apply(SetPotionFunction.setPotion(potion));
            }
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                LootItem.lootTableItem(factory.fromItem).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.fromCount))),
                EntryGroup.list(entries)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.EnchantBookForEmeralds factory) {
            EnchantRandomlyLootFunction.Builder enchantRandomlyFunction = new EnchantRandomlyLootFunction.Builder()
                .minLevel(factory.minLevel).maxLevel(factory.maxLevel)
                .include(server.registryAccess().registryOrThrow(Registries.ENCHANTMENT).getOrCreateTag(factory.tradeableEnchantments));
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
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
                LootItem.lootTableItem(Items.BOOK),
                LootItem.lootTableItem(Items.BOOK).apply(enchantRandomlyFunction)
            ).traderExperience(factory.villagerXp)};
        } else if (original instanceof VillagerTrades.TreasureMapForEmeralds factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.emeraldCost))),
                LootItem.lootTableItem(Items.COMPASS),
                LootItem.lootTableItem(Items.MAP)
                    .apply(new ExplorationMapFunction.Builder().setSearchRadius(100).setMapDecoration(factory.destinationType).setDestination(factory.destination).setZoom((byte) 2).setSkipKnownStructures(true))
                    .apply(SetNameFunction.setName(Component.translatable(factory.displayName), SetNameFunction.Target.ITEM_NAME))
            ).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.DyedArmorForEmeralds factory) {
            return new BehaviorTrade.Builder[]{new BehaviorTrade.Builder(
                LootItem.lootTableItem(Items.EMERALD).apply(SetItemCountFunction.setCount(ConstantValue.exactly(factory.value))),
                LootItem.lootTableItem(factory.item)
                    .apply(new SetDyeFunction.Builder(true))
                    .apply(new SetDyeFunction.Builder(true).when(LootItemRandomChanceCondition.randomChance(0.3f)))
                    .apply(new SetDyeFunction.Builder(true).when(LootItemRandomChanceCondition.randomChance(0.2f)))

            ).traderExperience(factory.villagerXp).maxUses(factory.maxUses)};
        } else if (original instanceof VillagerTrades.TypeSpecificTrade factory) {
            List<BehaviorTrade.Builder> trades = new ArrayList<>(factory.trades().size());
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

    private void enchantItem(LootPoolSingletonContainer.Builder<?> builder, Optional<ResourceKey<EnchantmentProvider>> optional, VillagerTrades.ItemListing factory) {
        if (optional.isPresent()) {
            EnchantmentProvider enchantmentProvider = server.registryAccess().registryOrThrow(Registries.ENCHANTMENT_PROVIDER).get(optional.get());
            if (enchantmentProvider instanceof SingleEnchantment singleEnchantment && singleEnchantment.level() instanceof ConstantInt constantInt) {
                builder.apply(new SetEnchantmentsFunction.Builder().withEnchantment(singleEnchantment.enchantment(), new ConstantValue(constantInt.getValue())));
            } else {
                LOGGER.warn("Failed to generate trades '{}', encountered unexpected enchantment provider '{}'.", factory, enchantmentProvider);
            }
        }
    }

    private static LootPoolSingletonContainer.Builder<?> lootTableItemStack(ItemStack itemStack) {
        LootPoolSingletonContainer.Builder<?> builder = LootItem.lootTableItem(itemStack.getItem());
        // Item Count
        if (itemStack.getCount() != 1) {
            builder.apply(SetItemCountFunction.setCount(ConstantValue.exactly(itemStack.getCount())));
        }
        // Enchantments
        ItemEnchantments enchantments = itemStack.getOrDefault(DataComponents.ENCHANTMENTS, ItemEnchantments.EMPTY);
        enchantments.entrySet().forEach(holderEntry -> {
            builder.apply(new SetEnchantmentsFunction.Builder(true).withEnchantment(holderEntry.getKey(), ConstantValue.exactly(holderEntry.getIntValue())));
        });
        // Potion effects
        PotionContents potionContents = itemStack.getOrDefault(DataComponents.POTION_CONTENTS, PotionContents.EMPTY);
        potionContents.potion().ifPresent(potionHolder -> {
            builder.apply(SetPotionFunction.setPotion(potionHolder));
        });
        return builder;
    }

    public enum OfferCountType {
        VILLAGER(i -> 2), WANDERING_TRADER(WANDERING_TRADER_COUNT), EXPERIMENTAL_WANDERING_TRADER(EXPERIMENTAL_WANDERING_TRADER_COUNT);

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
