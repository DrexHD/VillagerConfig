package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.json.data.TradeTable;
import me.drex.villagerconfig.util.TradeManager;
import me.drex.villagerconfig.util.TradeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTraderEntity.class)
public abstract class WanderingTraderEntityMixin extends MerchantEntity {


    public WanderingTraderEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "fillRecipes", at = @At("HEAD"), cancellable = true)
    public void replaceTrades(CallbackInfo ci) {
        TradeTable tradeTable = getTradeTable();
        if (tradeTable != null) {
            // Cancel vanilla trades
            ci.cancel();
            for (int level = 0; level < tradeTable.maxLevel(); level++) {
                TradeOffers.Factory[] tradeOffers = tradeTable.getTradeOffers(level, this.random);
                TradeOfferList tradeOfferList = this.getOffers();
                this.fillRecipesFromPool(tradeOfferList, tradeOffers, tradeOffers.length);
            }
        }
    }

    private TradeTable getTradeTable() {
        if (this.world instanceof ServerWorld) {
            TradeManager tradeManager = VillagerConfig.TRADE_MANAGER;
            return tradeManager.getTrade(TradeProvider.WANDERING_TRADER_ID);
        }
        return null;
    }
}
