package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.IDataPackContents;
import me.drex.villagerconfig.util.IMinecraftServer;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {

    @Shadow
    private MinecraftServer.ResourceManagerHolder resourceManagerHolder;

    @Override
    public TradeManager getTradeManager() {
        return ((IDataPackContents) this.resourceManagerHolder.dataPackContents).getTradeManager();
    }
}
