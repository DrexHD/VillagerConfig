package me.drex.villagerconfig.neoforge.util;

import me.drex.villagerconfig.common.util.TradeManager;
import net.minecraft.core.HolderLookup;
//? if < 1.21.2 {
import com.google.gson.JsonElement;
import net.minecraft.resources.RegistryOps;
//?}

public class NeoForgeTradeManager extends TradeManager {
    public NeoForgeTradeManager(HolderLookup.Provider provider) {
        super(provider);
    }

    //? if < 1.21.2 {
    @Override
    public RegistryOps<JsonElement> registryOps() {
        return makeConditionalOps();
    }
    //?}
}
