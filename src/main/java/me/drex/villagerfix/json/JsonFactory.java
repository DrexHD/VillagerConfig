package me.drex.villagerfix.json;

import com.google.gson.*;
import it.unimi.dsi.fastutil.ints.Int2ObjectArrayMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
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
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.village.VillagerType;
import net.minecraft.world.gen.feature.StructureFeature;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class JsonFactory {

    private final Gson gson;
    public final Map<String, Int2ObjectMap<TradeOffers.Factory[]>> TRADES = new HashMap<>();
    private static final Map<String, Class<? extends TradeOffers.Factory>> FACTORYNAME_TO_CLASS = new HashMap<>();
    private static final Path TRADES_PATH = VillagerFix.DATA_PATH.resolve("trades");
    private static final String WANDERING_TRADER_ID = "wandering_trader";

    public JsonFactory() {
        final GsonBuilder gsonBuilder = new GsonBuilder().setPrettyPrinting();
        customTypeAdapters(gsonBuilder);
        this.gson = gsonBuilder.create();
    }

    private void customTypeAdapters(GsonBuilder gsonBuilder) {
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

    public void saveTradeData() {
        try {
            Files.createDirectories(TRADES_PATH);
        } catch (IOException e) {
            VillagerFix.LOGGER.error("Could not trades directory!", e);
            return;
        }

        // Save all villager trades
        for (VillagerProfession villagerProfession : Registry.VILLAGER_PROFESSION) {
            this.saveTradeData(toFileName(villagerProfession), TradeOffers.PROFESSION_TO_LEVELED_TRADE.getOrDefault(villagerProfession, new Int2ObjectArrayMap<>()));
        }

        // Save wandering trader trades
        this.saveTradeData(WANDERING_TRADER_ID, TradeOffers.WANDERING_TRADER_TRADES);
    }

    public static String toFileName(VillagerProfession profession) {
        return URLEncoder.encode(profession.toString(), StandardCharsets.UTF_8);
    }

    private JsonArray serializeData(Int2ObjectMap<TradeOffers.Factory[]> trades) {
        JsonArray levels = new JsonArray();
        trades.forEach((i, factories) -> {
            JsonArray level = new JsonArray();
            for (final TradeOffers.Factory factory : factories) {
                final String factoryName = getHumanReadableClassName(factory.getClass());
                final JsonObject jsonObject = gson.toJsonTree(factory).getAsJsonObject();
                jsonObject.addProperty("type", factoryName);
                level.add(jsonObject);
            }
            levels.add(level);
        });
        return levels;
    }

    private void saveTradeData(String name, Int2ObjectMap<TradeOffers.Factory[]> trades) {
        try {
            JsonArray jsonArray = serializeData(trades);
            try {
                final Path jsonPath = TRADES_PATH.resolve(name + ".json");
                if (!jsonPath.toFile().exists()) {
                    VillagerFix.LOGGER.info("Saving trade offer data for " + name);
                    Files.write(jsonPath, gson.toJson(jsonArray).getBytes());
                }
            } catch (IOException e) {
                VillagerFix.LOGGER.error("Couldn't save \"" + name + "\"", e);
            }
        } catch (Exception e) {
            VillagerFix.LOGGER.error("An error occurred while serializing trade data for \"" + name + "\" trade customization wont work for this villager.");
            VillagerFix.LOGGER.error("Please join the support discord (discord.gg/HeZayd6SxF) or open an issue with the following stack trace at https://github.com/DrexHD/VillagerFix/issues", e);
        }
    }

    @SafeVarargs
    public final void addCustomTradeFactories(Class<? extends TradeOffers.Factory>... classes) {
        for (Class<? extends TradeOffers.Factory> clazz : classes) {
            this.getHumanReadableClassName(clazz);
        }
    }

    private String getHumanReadableClassName(Class<? extends TradeOffers.Factory> clazz) {
        final String className = clazz.getName();
        final String deobfuscated = Deobfuscator.deobfuscate(className);
        boolean isObfuscated = !deobfuscated.equals(className);
        if (isObfuscated) {
            final String humanReadable = deobfuscated.replaceAll("(?:[\\w]+\\.)+[\\w]+[?:$|.]([\\w]+)", "$1");
            FACTORYNAME_TO_CLASS.put(humanReadable, clazz);
            return humanReadable;
        } else {
            FACTORYNAME_TO_CLASS.put(clazz.getSimpleName(), clazz);
            return clazz.getSimpleName();
        }
    }

    public Int2ObjectMap<TradeOffers.Factory[]> getWanderingTraderOffers(Int2ObjectMap<TradeOffers.Factory[]> fallback) {
        return TRADES.getOrDefault(WANDERING_TRADER_ID, fallback);
    }

    public Int2ObjectMap<TradeOffers.Factory[]> getTradeOffers(VillagerProfession profession, Int2ObjectMap<TradeOffers.Factory[]> fallback) {
        return TRADES.getOrDefault(toFileName(profession), fallback);
    }

    public void loadTrades() {
        Map<String, Int2ObjectMap<TradeOffers.Factory[]>> trades = new HashMap<>(TRADES);
        TRADES.clear();
        for (VillagerProfession villagerProfession : Registry.VILLAGER_PROFESSION) {
            loadTrades(toFileName(villagerProfession));
        }
        loadTrades("wandering_trader");
    }

    private void loadTrades(String name) {
        try {
            Int2ObjectMap<TradeOffers.Factory[]> trades = attemptLoadTrades(name);
            if (trades != null) TRADES.put(name, trades);
        } catch (Exception e) {
            VillagerFix.LOGGER.error("An error occurred, while loading trade data for \"" + name + "\"", e);
        }
    }

    private Int2ObjectMap<TradeOffers.Factory[]> attemptLoadTrades(String name) throws IOException {
        Int2ObjectMap<TradeOffers.Factory[]> trades = new Int2ObjectArrayMap<>();
        Path path = TRADES_PATH.resolve(name + ".json");
        if (path.toFile().exists()) {
            final JsonArray[] levels = gson.fromJson(new String(Files.readAllBytes(path)), JsonArray[].class);
            int level = 1;
            for (JsonArray jsonLevelArray : levels) {
                TradeOffers.Factory[] factories = new TradeOffers.Factory[jsonLevelArray.size()];
                int offer = 0;
                for (JsonElement jsonTradeFactory : jsonLevelArray) {
                    factories[offer] = parseTradeOffer(jsonTradeFactory);
                    offer++;
                }
                trades.put(level, factories);
                level++;
            }
        } else {
            return null;
        }
        return trades;
    }

    private TradeOffers.Factory parseTradeOffer(JsonElement factoryJson) {
        final JsonObject jsonObject = factoryJson.getAsJsonObject();
        final String type = jsonObject.get("type").getAsString();
        final Class<? extends TradeOffers.Factory> clazz = FACTORYNAME_TO_CLASS.get(type);
        if (clazz == null) {
            throw new IllegalArgumentException("Unknown trade type \"" + type + "\"");
        }
        jsonObject.remove("type");
        final Object obj = gson.fromJson(jsonObject.toString(), clazz);
        if (obj instanceof TradeOffers.Factory factory) {
            return factory;
        } else {
            // This should never happen
            throw new UnknownError();
        }
    }

    private void printTradeOffer(JsonObject offer) {
        VillagerFix.LOGGER.info("Suspected offer:");
        VillagerFix.LOGGER.info(offer.toString());
    }

}