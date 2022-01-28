package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.IServerResourceManager;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.resource.ReloadableResourceManager;
import net.minecraft.resource.ServerResourceManager;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerResourceManager.class)
public abstract class ServerResourceManagerMixin implements IServerResourceManager {

    @Shadow
    @Final
    private ReloadableResourceManager resourceManager;
    private final TradeManager tradeManager = new TradeManager();

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    public void addTradeManager(DynamicRegistryManager registryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, CallbackInfo ci) {
        this.resourceManager.registerReloader(this.tradeManager);
    }

    @Override
    public TradeManager getTradeManager() {
        return this.tradeManager;
    }

}
