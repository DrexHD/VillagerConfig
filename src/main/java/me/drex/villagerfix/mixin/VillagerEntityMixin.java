package me.drex.villagerfix.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.config.ConfigEntries;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @Redirect(
            method = "fillRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    @SuppressWarnings("unchecked")
    public <V> V putCustomRecipes(Map map, Object key) {
        return (V) VillagerFix.getJsonFactory().getTradeOffers((VillagerProfession) key, (Int2ObjectMap<TradeOffers.Factory[]>) map.get(key));
    }

    @Inject(
            method = "canRefreshTrades",
            at = @At("RETURN"),
            cancellable = true
    )
    public void removeRefreshTradesInfo(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigEntries.oldTrades.enabled) cir.setReturnValue(false);
    }

}
