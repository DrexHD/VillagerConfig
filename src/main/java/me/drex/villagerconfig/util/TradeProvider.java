package me.drex.villagerconfig.util;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.IntUnaryOperator;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.json.TradeGsons;
import me.drex.villagerconfig.json.behavior.*;
import me.drex.villagerconfig.json.behavior.item.ChoiceItem;
import me.drex.villagerconfig.json.behavior.item.TradeItem;
import me.drex.villagerconfig.json.behavior.item.WantItem;
import me.drex.villagerconfig.mixin.VillagerDataAccessor;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.EntityPropertiesLootCondition;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.RandomChanceLootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.*;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.UniformLootNumberProvider;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.potion.Potion;
import net.minecraft.predicate.NbtPredicate;
import net.minecraft.predicate.entity.EntityPredicate;
import net.minecraft.recipe.BrewingRecipeRegistry;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.DynamicRegistryManager;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.VILLAGER;
import static me.drex.villagerconfig.util.TradeProvider.OfferCountType.WANDERING_TRADER;

public class TradeProvider implements DataProvider {

    private static final Logger LOGGER = VillagerConfig.LOGGER;
    private final Gson gson;
    public static final Identifier WANDERING_TRADER_ID = new Identifier("wanderingtrader");
    private static final IntUnaryOperator WANDERING_TRADER_COUNT = i -> switch (i) {
        case 1 -> 5;
        case 2 -> 1;
        default -> 0;
    };

    private final DataGenerator root;

    public TradeProvider(DynamicRegistryManager registryManager, DataGenerator root) {
        this.root = root;
        this.gson = TradeGsons.getTradeGsonBuilder(registryManager).setPrettyPrinting().create();
    }

    @Override
    public void run(DataWriter cache) {
        // Save all villager trades
        for (VillagerProfession villagerProfession : Registry.VILLAGER_PROFESSION) {
            this.saveMerchantTrades(cache, Registry.VILLAGER_PROFESSION.getId(villagerProfession), TradeOffers.PROFESSION_TO_LEVELED_TRADE.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>()), VILLAGER);
        }
        // Save wandering trader trades
        this.saveMerchantTrades(cache, WANDERING_TRADER_ID, TradeOffers.WANDERING_TRADER_TRADES, WANDERING_TRADER);
    }

    private void saveMerchantTrades(DataWriter cache, Identifier merchantId, Int2ObjectMap<TradeOffers.Factory[]> trades, OfferCountType offerCountType) {
        Path path = getOutput(this.root.getOutput(), merchantId);
        JsonElement jsonElements;
        try {
            jsonElements = serializeData(trades, offerCountType);
        } catch (Exception e) {
            LOGGER.error("Couldn't serialize trade data {}", path, e);
            return;
        }
        try {
            DataProvider.writeToPath(cache, jsonElements, path);
        } catch (IOException e) {
            LOGGER.error("Couldn't save trade data {}", path, e);
        }
    }

    @Override
    public String getName() {
        return "Trades";
    }

    private static Path getOutput(Path rootOutput, Identifier merchantId) {
        return rootOutput.resolve("data/" + merchantId.getNamespace() + "/trades/" + merchantId.getPath() + ".json");
    }

    private JsonElement serializeData(Int2ObjectMap<TradeOffers.Factory[]> trades, OfferCountType offerCountType) {
        int levels = trades.size();
        final TradeTier[] tiers = new TradeTier[levels];
        trades.forEach((level, factoryArr) -> {
            TradeGroup tradeGroup = new TradeGroup(offerCountType.getOfferCount(level), Arrays.stream(factoryArr).map(this::convert).toList().toArray(new TradeOffers.Factory[]{}));
            tiers[level - 1] = new TradeTier((VillagerDataAccessor.getLevelBaseExperience()[level - 1]), new TradeGroup[]{tradeGroup}, null);
        });
        TradeTable tradeTable = new TradeTable(tiers);
        return gson.toJsonTree(tradeTable);
    }

    private TradeOffers.Factory convert(TradeOffers.Factory original) {
        if (original instanceof TradeOffers.BuyForOneEmeraldFactory factory) {
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(factory.buy, null, ConstantLootNumberProvider.create(factory.price), ConstantLootNumberProvider.create(factory.multiplier), null)
                    },
                    new TradeItem[]{
                            new TradeItem(Items.EMERALD, null, ConstantLootNumberProvider.create(1), null)
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        } else if (original instanceof TradeOffers.SellItemFactory factory) {
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(Items.EMERALD, null, ConstantLootNumberProvider.create(factory.price), ConstantLootNumberProvider.create(factory.multiplier), null)
                    },
                    new TradeItem[]{
                            new TradeItem(factory.sell.getItem(), null, ConstantLootNumberProvider.create(factory.count), null)
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        } else if (original instanceof TradeOffers.SellSuspiciousStewFactory factory) {
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(Items.EMERALD, null, ConstantLootNumberProvider.create(1), ConstantLootNumberProvider.create(factory.multiplier), null)
                    },
                    new TradeItem[]{
                            new TradeItem(Items.SUSPICIOUS_STEW, null, ConstantLootNumberProvider.create(1), new LootFunction[]{
                                    new SetStewEffectLootFunction.Builder().withEffect(factory.effect, ConstantLootNumberProvider.create(factory.duration)).build()
                            })
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(12),
                    true
            );
        } else if (original instanceof TradeOffers.ProcessItemFactory factory) {
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(Items.EMERALD, null, ConstantLootNumberProvider.create(factory.price), ConstantLootNumberProvider.create(factory.multiplier), null),
                            new WantItem(factory.secondBuy.getItem(), null, ConstantLootNumberProvider.create(factory.secondCount), ConstantLootNumberProvider.create(factory.multiplier), null)
                    },
                    new TradeItem[]{
                            new TradeItem(factory.sell.getItem(), null, ConstantLootNumberProvider.create(factory.sellCount), null)
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        } else if (original instanceof TradeOffers.SellEnchantedToolFactory factory) {
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(Items.EMERALD, null, UniformLootNumberProvider.create(factory.basePrice + 5, factory.basePrice + 19), ConstantLootNumberProvider.create(factory.multiplier), null)
                    },
                    new TradeItem[]{
                            new TradeItem(factory.tool.getItem(), null, ConstantLootNumberProvider.create(1), new LootFunction[]{
                                    new EnchantWithLevelsLootFunction.Builder(UniformLootNumberProvider.create(5, 19)).build()
                            })
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        } else if (original instanceof TradeOffers.TypeAwareBuyForOneEmeraldFactory factory) {
            ChoiceItem[] buyChoice = new ChoiceItem[Registry.VILLAGER_TYPE.size()];
            int i = 0;
            for (VillagerType villagerType : Registry.VILLAGER_TYPE) {
                NbtCompound root = new NbtCompound();
                NbtCompound villagerData = new NbtCompound();
                villagerData.putString("type", Registry.VILLAGER_TYPE.getId(villagerType).toString());
                root.put("VillagerData", villagerData);
                buyChoice[i] = new ChoiceItem(factory.map.get(villagerType), null, ConstantLootNumberProvider.create(factory.count), null, new LootCondition[]{
                        EntityPropertiesLootCondition.builder(LootContext.EntityTarget.THIS, EntityPredicate.Builder.create().nbt(new NbtPredicate(root))).build()
                });
                i++;
            }
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(null, buyChoice, null, null, null)
                    },
                    new TradeItem[]{
                            new TradeItem(Items.EMERALD, null, ConstantLootNumberProvider.create(1), null)
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        } else if (original instanceof TradeOffers.SellPotionHoldingItemFactory factory) {
            List<Potion> potions = Registry.POTION.stream().filter(potion -> !potion.getEffects().isEmpty() && BrewingRecipeRegistry.isBrewable(potion)).collect(Collectors.toList());
            ChoiceItem[] sellChoice = new ChoiceItem[potions.size()];
            for (int i = 0; i < potions.size(); i++) {
                Potion potion = potions.get(i);
                sellChoice[i] = new ChoiceItem(factory.sell.getItem(), null, ConstantLootNumberProvider.create(factory.sellCount),
                        new LootFunction[]{SetPotionLootFunction.builder(potion).build()}, null);
            }
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(Items.EMERALD, null, ConstantLootNumberProvider.create(factory.price), ConstantLootNumberProvider.create(factory.priceMultiplier), null),
                            new WantItem(factory.secondBuy, null, ConstantLootNumberProvider.create(factory.secondCount), ConstantLootNumberProvider.create(factory.priceMultiplier), null)
                    },
                    new TradeItem[]{
                            new TradeItem(null, sellChoice, null, null)
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        } else if (original instanceof TradeOffers.EnchantBookFactory factory) {
            return new VC_EnchantBookFactory(new VC_EnchantBookFactory.Enchantments(true, null, null), ConstantLootNumberProvider.create(2), null, null, null, null, ConstantLootNumberProvider.create(factory.experience), ConstantLootNumberProvider.create(12), true, new WantItem[]{
                    new WantItem(Items.BOOK, null, ConstantLootNumberProvider.create(1), ConstantLootNumberProvider.create(0.2F), null),
                    new WantItem(Items.EMERALD, null, ConstantLootNumberProvider.create(1), ConstantLootNumberProvider.create(0.2F), null)
            });
        } else if (original instanceof TradeOffers.SellMapFactory factory) {
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(Items.EMERALD, null, ConstantLootNumberProvider.create(factory.price), ConstantLootNumberProvider.create(0.2F), null),
                            new WantItem(Items.COMPASS, null, ConstantLootNumberProvider.create(1), ConstantLootNumberProvider.create(0.2F), null)
                    },
                    new TradeItem[]{
                            new TradeItem(Items.MAP, null, ConstantLootNumberProvider.create(1), new LootFunction[]{
                                    new ExplorationMapLootFunction.Builder().searchRadius(100).withDecoration(factory.iconType).withDestination(factory.structure).withZoom((byte) 2).withSkipExistingChunks(true).build()
                            })
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        }
        else if (original instanceof TradeOffers.SellDyedArmorFactory factory) {
            return new BehaviorTrade(
                    new WantItem[]{
                            new WantItem(Items.EMERALD, null, ConstantLootNumberProvider.create(factory.price), ConstantLootNumberProvider.create(0.2F), null),
                            new WantItem(factory.sell, null, ConstantLootNumberProvider.create(1), ConstantLootNumberProvider.create(0.2F), null)
                    },
                    new TradeItem[]{
                            new TradeItem(factory.sell, null, ConstantLootNumberProvider.create(1), new LootFunction[]{
                                    new SetDyeFunction(new LootCondition[]{}, SetDyeFunction.Dye.random(), true),
                                    new SetDyeFunction(new LootCondition[]{
                                            RandomChanceLootCondition.builder(30).build()
                                    }, SetDyeFunction.Dye.random(), true),
                                    new SetDyeFunction(new LootCondition[]{
                                            RandomChanceLootCondition.builder(20).build()
                                    }, SetDyeFunction.Dye.random(), true),
                            })
                    },
                    ConstantLootNumberProvider.create(factory.experience),
                    ConstantLootNumberProvider.create(factory.maxUses),
                    true
            );
        }
        return original;
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

}
