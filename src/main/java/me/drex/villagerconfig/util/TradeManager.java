package me.drex.villagerconfig.util;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Multimap;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.json.data.TradeTable;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.loot.LootTableReporter;
import net.minecraft.resource.JsonDataLoader;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TradeManager extends JsonDataLoader implements IdentifiableResourceReloadListener {

    private static final Logger LOGGER = VillagerConfig.LOGGER;
    private final Gson gson;
    private Map<Identifier, TradeTable> trades = ImmutableMap.of();

    public TradeManager(Gson gson) {
        super(gson, "trades");
        this.gson = gson;
    }

    @Nullable
    public TradeTable getTrade(Identifier id) {
        return trades.get(id);
    }

    @Override
    protected void apply(Map<Identifier, JsonElement> prepared, ResourceManager manager, Profiler profiler) {
        ImmutableMap.Builder<Identifier, TradeTable> builder = ImmutableMap.builder();
        prepared.forEach((identifier, jsonElement) -> {
            try {
                TradeTable table = gson.fromJson(jsonElement, TradeTable.class);
                builder.put(identifier, table);
            } catch (Exception exception) {
                LOGGER.error("Failed to load trade {}", identifier, exception);
            }
        });

        this.trades = builder.build();
        LOGGER.info("Loaded {} trades", trades.size());
    }

    @Override
    public Identifier getFabricId() {
        return new Identifier(VillagerConfig.MOD_ID, "trades");
    }
}
