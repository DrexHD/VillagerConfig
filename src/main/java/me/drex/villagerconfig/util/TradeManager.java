package me.drex.villagerconfig.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;
import com.mojang.serialization.JsonOps;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.data.TradeTable;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.Util;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TradeManager extends SimpleJsonResourceReloadListener implements IdentifiableResourceReloadListener {

    private static final Logger LOGGER = VillagerConfig.LOGGER;
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();

    private Map<ResourceLocation, TradeTable> trades = ImmutableMap.of();

    public TradeManager() {
        super(GSON, "trades");
    }

    @Nullable
    public TradeTable getTrade(ResourceLocation id) {
        return trades.get(id);
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, @NotNull ResourceManager manager, @NotNull ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, TradeTable> builder = ImmutableMap.builder();
        prepared.forEach((identifier, jsonElement) -> {
            try {
                TradeTable table = Util.getOrThrow(TradeTable.CODEC.parse(JsonOps.INSTANCE, jsonElement), JsonParseException::new);
                builder.put(identifier, table);
            } catch (Exception exception) {
                LOGGER.error("Failed to load trade {}", identifier, exception);
            }
        });

        this.trades = builder.build();
        LOGGER.info("Loaded {} trades", trades.size());
    }

    @Override
    public ResourceLocation getFabricId() {
        return new ResourceLocation(VillagerConfig.MOD_ID, "trades");
    }
}
