package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.IDataPackContents;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.server.DataPackContents;
import net.minecraft.server.command.CommandManager;
import net.minecraft.util.registry.DynamicRegistryManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin implements IDataPackContents {

    private TradeManager tradeManager;

    @Override
    public TradeManager getTradeManager() {
        return this.tradeManager;
    }

    @Inject(
            method = "<init>",
            at = @At("TAIL")
    )
    public void onInit(DynamicRegistryManager.Immutable dynamicRegistryManager, CommandManager.RegistrationEnvironment commandEnvironment, int functionPermissionLevel, CallbackInfo ci) {
        this.tradeManager = new TradeManager(dynamicRegistryManager);
    }

    @Redirect(
            method = "getContents",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/List;of(Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;Ljava/lang/Object;)Ljava/util/List;"
            )
    )
    public <E> List<E> addTradeManager(E e1, E e2, E e3, E e4, E e5, E e6, E e7) {
        // This will likely cause mod incompatibility (is there an api for this?)
        return List.of(e1, e2, e3, e4, e5, e6, e7, (E) this.tradeManager);
    }

}
