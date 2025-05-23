package me.drex.villagerconfig.util;

import com.google.common.collect.ImmutableMap;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.data.TradeTable;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public class TradeManager extends SimpleJsonResourceReloadListener/*? if >= 1.21.2 {*/ <TradeTable> /*?}*/ implements IdentifiableResourceReloadListener {

    private static final Logger LOGGER = VillagerConfig.LOGGER;

    private Map<ResourceLocation, TradeTable> trades = ImmutableMap.of();
    //? if < 1.21.2 {
    /*private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private final HolderLookup.Provider provider;
    *///?}

    public TradeManager(HolderLookup.Provider provider) {
        //? if >= 1.21.2 {
        super(provider.createSerializationContext(JsonOps.INSTANCE), TradeTable.CODEC, FileToIdConverter.json("trades"));
        //?} else {
        /*super(GSON, "trades");
        this.provider = provider;
        *///?}
    }

    @Nullable
    public TradeTable getTrade(ResourceLocation id) {
        return trades.get(id);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(VillagerConfig.MOD_ID, "trades");
    }

    //? if >= 1.21.2 {
    @Override
    protected void apply(Map<ResourceLocation, TradeTable> prepared, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.trades = prepared;
        LOGGER.info("Loaded {} trades", trades.size());
    }
    //?} else {
    /*@Override
    protected void apply(Map<ResourceLocation, JsonElement> prepared, ResourceManager manager, ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, TradeTable> builder = ImmutableMap.builder();
        prepared.forEach((identifier, jsonElement) -> {
            try {
                TradeTable table = TradeTable.CODEC.parse(provider.createSerializationContext(JsonOps.INSTANCE), jsonElement).getOrThrow();
                builder.put(identifier, table);
            } catch (Exception exception) {
                LOGGER.error("Failed to load trade {}", identifier, exception);
            }
        });

        this.trades = builder.build();
        LOGGER.info("Loaded {} trades", trades.size());
    }
    *///?}
}
