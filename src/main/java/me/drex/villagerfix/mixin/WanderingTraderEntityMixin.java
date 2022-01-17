package me.drex.villagerfix.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.util.IMinecraftServer;
import me.drex.villagerfix.util.TradeManager;
import me.drex.villagerfix.util.TradeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin extends MerchantEntity {


    public WanderingTraderEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

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
        if (this.world instanceof ServerWorld serverWorld) {
            TradeManager tradeManager = ((IMinecraftServer) serverWorld.getServer()).getTradeManager();
            Int2ObjectMap<TradeOffers.Factory[]> trade = tradeManager.getTrade(TradeProvider.WANDERING_TRADER_ID);
            return trade != null ? (V) trade.get(key) : (V) int2ObjectMap.get(key);
        }
        return (V) int2ObjectMap.get(key);
    }

}
