package me.drex.villagerfix.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.VillagerFix;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin {

    @Redirect(
            method = "fillRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lit/unimi/dsi/fastutil/ints/Int2ObjectMap;get(I)Ljava/lang/Object;",
                    remap = false
            )
    )
    @SuppressWarnings("unchecked")
    public <V> V putCustomRecipes(Int2ObjectMap<TradeOffers.Factory[]> int2ObjectMap, int key) {
        return (V) VillagerFix.getJsonFactory().getWanderingTraderOffers(int2ObjectMap).get(key);
    }

}
