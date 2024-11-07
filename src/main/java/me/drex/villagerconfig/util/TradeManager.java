package me.drex.villagerconfig.util;

import com.google.common.collect.ImmutableMap;
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

public class TradeManager extends SimpleJsonResourceReloadListener<TradeTable> implements IdentifiableResourceReloadListener {

    private static final Logger LOGGER = VillagerConfig.LOGGER;

    private Map<ResourceLocation, TradeTable> trades = ImmutableMap.of();

    public TradeManager(HolderLookup.Provider provider) {
        super(provider.createSerializationContext(JsonOps.INSTANCE), TradeTable.CODEC, FileToIdConverter.json("trades"));
    }

    @Nullable
    public TradeTable getTrade(ResourceLocation id) {
        return trades.get(id);
    }

    @Override
    public ResourceLocation getFabricId() {
        return ResourceLocation.fromNamespaceAndPath(VillagerConfig.MOD_ID, "trades");
    }

    @Override
    protected void apply(Map<ResourceLocation, TradeTable> prepared, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.trades = prepared;
        LOGGER.info("Loaded {} trades", trades.size());
    }
}
