package me.drex.villagerconfig.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.json.TradeGsons;
import me.drex.villagerconfig.json.behavior.TradeTable;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TradeManager extends JsonDataLoader {

    private static final Logger LOGGER = VillagerConfig.LOGGER;
    private static final Gson GSON = TradeGsons.getTradeGsonBuilder().create();
    private Map<Identifier, TradeTable> trades = ImmutableMap.of();
    // Not the cleanest solution, but it works...
    private static DynamicRegistryManager registryManager;

    public TradeManager(DynamicRegistryManager registryManager) {
        super(GSON, "trades");
        TradeManager.registryManager = registryManager;
    }

    public static DynamicRegistryManager getRegistryManager() {
        return registryManager;
    }

    @Nullable
    public TradeTable getTrade(Identifier id) {
        return trades.get(id);
    }

    /**
     * Handles the prepared intermediate object.
     *
     * <p>This method is called in the apply executor, or the game engine, in a
     * reload.
     *
     * @param prepared the prepared object
     * @param manager  the resource manager
     * @param profiler the apply profiler
     */
    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, TradeTable> builder = ImmutableMap.builder();
        TradeTableReporter root = new TradeTableReporter();
        prepared.forEach((identifier, jsonElement) -> {
            try {
                TradeTable table = GSON.fromJson(jsonElement, TradeTable.class);
                TradeTableReporter tradeTableReporter = root.withTable("{" + identifier + "}");
                table.validate(tradeTableReporter);
                Multimap<String, String> errors = tradeTableReporter.getErrors();
                if (!errors.isEmpty()) {
                    // Throw exception, of the first problem
                    errors.forEach((key, value) -> {
                        throw new IllegalArgumentException("Found validation problem in " + key + ": " + value);
                    });
                } else {
                    builder.put(identifier, table);
                    tradeTableReporter.getWarnings().forEach((key, value) -> {
                        LOGGER.warn("Found small problem in {}: {}", key, value);
                    });
                }
            } catch (Exception exception) {
                LOGGER.error("Failed to load trade {}", identifier, exception);
            }
        });

        this.trades = builder.build();
        LOGGER.info("Loaded {} trades", trades.size());
    }

}
