package me.drex.villagerconfig.common.util;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.JsonOps;
import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.data.TradeTable;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.FileToIdConverter;
import net.minecraft.resources.Identifier;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.profiling.ProfilerFiller;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Map;

public class TradeManager extends SimpleJsonResourceReloadListener <TradeTable>  {

    public static final Identifier ID = Identifier.fromNamespaceAndPath(VillagerConfig.MOD_ID, "trades");
    private static final Logger LOGGER = VillagerConfig.LOGGER;

    private Map<Identifier, TradeTable> trades = ImmutableMap.of();

    public TradeManager(HolderLookup.Provider provider) {
        super(provider.createSerializationContext(JsonOps.INSTANCE), TradeTable.CODEC, FileToIdConverter.json("trades"));
    }

    @Nullable
    public TradeTable getTrade(Identifier id) {
        return trades.get(id);
    }

    @Override
    protected void apply(Map<Identifier, TradeTable> prepared, ResourceManager resourceManager, ProfilerFiller profilerFiller) {
        this.trades = prepared;
        LOGGER.info("Loaded {} trades", trades.size());

        this.trades.forEach((identifier, tradeTable) -> {
            ArrayList<Identifier> availableTypes = new ArrayList<>(BuiltInRegistries.VILLAGER_PROFESSION.keySet());
            availableTypes.add(TradeProvider.WANDERING_TRADER_ID);
            if (!availableTypes.contains(identifier)) {
                LOGGER.warn("Found trade for unknown villager type '{}'. Available types: {}", identifier, Arrays.toString(availableTypes.toArray()));
            }
        });
    }
}
