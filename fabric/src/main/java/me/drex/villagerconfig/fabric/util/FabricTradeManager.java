package me.drex.villagerconfig.fabric.util;

import me.drex.villagerconfig.common.util.TradeManager;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.Identifier;

public class FabricTradeManager extends TradeManager implements IdentifiableResourceReloadListener {
    public FabricTradeManager(HolderLookup.Provider provider) {
        super(provider);
    }

    @Override
    public Identifier getFabricId() {
        return ID;
    }
}
