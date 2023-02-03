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
import me.drex.villagerconfig.util.loot.number.AddLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.MultiplyLootNumberProvider;
import me.drex.villagerconfig.util.loot.number.ReferenceLootNumberProvider;
import me.drex.villagerconfig.util.loot.function.SetDyeFunction;
import me.drex.villagerconfig.util.loot.function.EnchantRandomlyLootFunction;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.AlternativeEntry;
import net.minecraft.loot.entry.GroupEntry;
import net.minecraft.loot.entry.ItemEntry;
import net.minecraft.loot.entry.LootPoolEntry;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.VILLAGER;
import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.WANDERING_TRADER;

public class TradeProvider implements DataProvider {

    private final DataOutput.PathResolver pathResolver;
    public static final Identifier WANDERING_TRADER_ID = new Identifier("wanderingtrader");
    private static final IntUnaryOperator WANDERING_TRADER_COUNT = i -> switch (i) {
        case 1 -> 5;
        case 2 -> 1;
        default -> 0;
    };

    public TradeProvider(DataOutput output) {
        this.pathResolver = output.getResolver(DataOutput.OutputType.DATA_PACK, "trades");
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        HashMap<Identifier, TradeData> map = Maps.newHashMap();

        // Save all villager trades
        for (VillagerProfession villagerProfession : Registries.VILLAGER_PROFESSION) {
            map.put(Registries.VILLAGER_PROFESSION.getId(villagerProfession), new TradeData(TradeOffers.PROFESSION_TO_LEVELED_TRADE.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>()), VILLAGER));
        }
        // Save wandering trader trades
        map.put(WANDERING_TRADER_ID, new TradeData(TradeOffers.WANDERING_TRADER_TRADES, WANDERING_TRADER));
        return CompletableFuture.allOf(map.entrySet().stream().map(entry -> {
            Identifier identifier = entry.getKey();
            TradeData tradeData = entry.getValue();
            Path path = this.pathResolver.resolveJson(identifier);
            return DataProvider.writeToPath(writer, toJson(tradeData), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "Trades";
    }

    private JsonElement toJson(TradeData tradeData) {
        int levels = tradeData.trades().size();
        final TradeTier[] tiers = new TradeTier[levels];
        tradeData.trades().forEach((level, factoryArr) -> {
            TradeGroup tradeGroup = new TradeGroup(tradeData.offerCountType().getOfferCount(level), Arrays.stream(factoryArr).map(this::convert).filter(Objects::nonNull).toArray(BehaviorTrade[]::new));
            tiers[level - 1] = new TradeTier((VillagerDataAccessor.getLevelBaseExperience()[level - 1]), new TradeGroup[]{tradeGroup});
        });
        TradeTable tradeTable = new TradeTable(tiers);
        return TradeGsons.GSON.toJsonTree(tradeTable);
    }

    private BehaviorTrade convert(TradeOffers.Factory original) {
        if (original instanceof TradeOffers.BuyForOneEmeraldFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(factory.buy).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.price))),
                    ItemEntry.builder(Items.EMERALD)
            ).priceMultiplier(factory.multiplier).traderExperience(factory.experience).maxUses(factory.maxUses).build();
        } else if (original instanceof TradeOffers.SellItemFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.price))),
                    ItemEntry.builder(factory.sell.getItem()).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.count)))
            ).priceMultiplier(factory.multiplier).traderExperience(factory.experience).maxUses(factory.maxUses).build();
        } else if (original instanceof TradeOffers.SellSuspiciousStewFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.EMERALD),
                    ItemEntry.builder(Items.SUSPICIOUS_STEW).apply(new SetStewEffectLootFunction.Builder().withEffect(factory.effect, ConstantLootNumberProvider.create(factory.duration)))
            ).traderExperience(factory.experience).build();
        } else if (original instanceof TradeOffers.ProcessItemFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.price))),
                    ItemEntry.builder(factory.secondBuy.getItem()).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.secondCount))),
                    ItemEntry.builder(factory.sell.getItem()).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.sellCount)))
            ).priceMultiplier(factory.multiplier).traderExperience(factory.experience).maxUses(factory.maxUses).build();
        } else if (original instanceof TradeOffers.SellEnchantedToolFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.EMERALD)
                            .apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.basePrice)))
                            .apply(SetCountLootFunction.builder(ReferenceLootNumberProvider.create("enchantLevel"), true)),
                    ItemEntry.builder(factory.tool.getItem()).apply(new EnchantWithLevelsLootFunction.Builder(ReferenceLootNumberProvider.create("enchantLevel")))
            ).priceMultiplier(factory.multiplier).traderExperience(factory.experience).maxUses(factory.maxUses).numberReference("enchantLevel", UniformLootNumberProvider.create(5, 19)).build();
        } else if (original instanceof TradeOffers.TypeAwareBuyForOneEmeraldFactory factory) {
            LootPoolEntry.Builder<?>[] children = new LootPoolEntry.Builder[Registries.VILLAGER_TYPE.size()];
            int i = 0;
            for (VillagerType villagerType : Registries.VILLAGER_TYPE) {
                NbtCompound root = new NbtCompound();
                NbtCompound villagerData = new NbtCompound();
                villagerData.putString("type", Registries.VILLAGER_TYPE.getId(villagerType).toString());
                root.put("VillagerData", villagerData);
                children[i] = ItemEntry.builder(factory.map.get(villagerType)).apply(
                        SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.count))
                ).conditionally(EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().nbt(new NbtPredicate(root))));
                i++;
            }
            return new BehaviorTrade.Builder(
                    AlternativeEntry.builder(children),
                    ItemEntry.builder(Items.EMERALD)
            ).priceMultiplier(0.05f).traderExperience(factory.experience).maxUses(factory.maxUses).build();
        } else if (original instanceof TradeOffers.SellPotionHoldingItemFactory factory) {
            List<Potion> potions = Registries.POTION.stream().filter(potion -> !potion.getEffects().isEmpty() && BrewingRecipeRegistry.isBrewable(potion)).toList();
            LootPoolEntry.Builder<?>[] entries = new LootPoolEntry.Builder[potions.size()];
            for (int i = 0; i < potions.size(); i++) {
                Potion potion = potions.get(i);
                entries[i] = ItemEntry.builder(factory.sell.getItem()).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.sellCount))).apply(SetPotionLootFunction.builder(potion));
            }
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.price))),
                    ItemEntry.builder(factory.secondBuy).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.secondCount))),
                    GroupEntry.create(entries)
            ).priceMultiplier(factory.priceMultiplier).traderExperience(factory.experience).maxUses(factory.maxUses).build();
        } else if (original instanceof TradeOffers.EnchantBookFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.BOOK),
                    ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(
                            // Count formula: (2 + (random.nextInt(5 + (enchantmentLevel * 10))) + (3 * enchantmentLevel)) * treasureMultiplier
                            // Treasure multiplier
                            MultiplyLootNumberProvider.create(
                                    // 2 + (random.nextInt(5 + (enchantmentLevel * 10))) + (3 * enchantmentLevel)
                                    AddLootNumberProvider.create(
                                            // 2
                                            ConstantLootNumberProvider.create(2),
                                            // (random.nextInt(5 + (enchantmentLevel * 10)))
                                            new UniformLootNumberProvider(
                                                    ConstantLootNumberProvider.create(5),
                                                    // enchantmentLevel * 10
                                                    MultiplyLootNumberProvider.create(
                                                            ReferenceLootNumberProvider.create("enchantmentLevel"),
                                                            ConstantLootNumberProvider.create(10)
                                                    )
                                            ),
                                            // (3 * enchantmentLevel)
                                            MultiplyLootNumberProvider.create(
                                                    ConstantLootNumberProvider.create(3),
                                                    ReferenceLootNumberProvider.create("enchantmentLevel")
                                            )

                                    ),
                                    // treasureMultiplier
                                    ReferenceLootNumberProvider.create("treasureMultiplier")
                            )

                    )),
                    ItemEntry.builder(Items.BOOK).apply(new EnchantRandomlyLootFunction.Builder().tradeEnchantments())
            ).traderExperience(factory.experience).build();
        } else if (original instanceof TradeOffers.SellMapFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.price))),
                    ItemEntry.builder(Items.COMPASS),
                    ItemEntry.builder(Items.MAP).apply(new ExplorationMapLootFunction.Builder().searchRadius(100).withDecoration(factory.iconType).withDestination(factory.structure).withZoom((byte) 2).withSkipExistingChunks(true))
            ).traderExperience(factory.experience).maxUses(factory.maxUses).build();
        } else if (original instanceof TradeOffers.SellDyedArmorFactory factory) {
            return new BehaviorTrade.Builder(
                    ItemEntry.builder(Items.EMERALD).apply(SetCountLootFunction.builder(ConstantLootNumberProvider.create(factory.price))),
                    ItemEntry.builder(factory.sell),
                    ItemEntry.builder(factory.sell)
                            .apply(new SetDyeFunction.Builder(true))
                            .apply(new SetDyeFunction.Builder(true).conditionally(RandomChanceLootCondition.builder(0.3f)))
                            .apply(new SetDyeFunction.Builder(true).conditionally(RandomChanceLootCondition.builder(0.2f)))
                            
            ).traderExperience(factory.experience).maxUses(factory.maxUses).build();
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

    public record TradeData(Int2ObjectMap<TradeOffers.Factory[]> trades, OfferCountType offerCountType) {

    }

}
