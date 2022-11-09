package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.json.TradeGsons;
import me.drex.villagerconfig.util.TradeManager;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resource.ResourceType;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.drex.villagerconfig.VillagerConfig.TRADE_MANAGER;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin {

    @Inject(method = "<init>", at = @At("TAIL"))
    public void registerTradeManager(DynamicRegistryManager.Immutable dynamicRegistryManager, FeatureSet enabledFeatures, CommandManager.RegistrationEnvironment environment, int functionPermissionLevel, CallbackInfo ci) {
        if (TRADE_MANAGER == null) {
            TRADE_MANAGER = new TradeManager(TradeGsons.getTradeGsonBuilder(dynamicRegistryManager).create());
            ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(TRADE_MANAGER);
        }
    }

}
