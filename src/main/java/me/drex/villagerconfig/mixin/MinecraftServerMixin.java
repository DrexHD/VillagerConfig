package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.IMinecraftServer;
import me.drex.villagerconfig.util.IServerResourceManager;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.MinecraftServer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MinecraftServer.class)
public abstract class MinecraftServerMixin implements IMinecraftServer {

    @Shadow private ServerResourceManager serverResourceManager;

    @Override
    public TradeManager getTradeManager() {
        return ((IServerResourceManager)this.serverResourceManager).getTradeManager();
    }
}
