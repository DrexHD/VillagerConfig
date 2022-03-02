package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.IDataPackContents;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.server.DataPackContents;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.List;

@Mixin(DataPackContents.class)
public abstract class DataPackContentsMixin implements IDataPackContents {

    private final TradeManager tradeManager = new TradeManager();

    @Override
    public TradeManager getTradeManager() {
        return this.tradeManager;
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
