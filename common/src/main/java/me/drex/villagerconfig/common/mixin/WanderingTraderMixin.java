package me.drex.villagerconfig.common.mixin;

import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.data.BehaviorTrade;
import me.drex.villagerconfig.common.data.TradeTable;
import me.drex.villagerconfig.common.util.TradeManager;
import me.drex.villagerconfig.common.util.TradeProvider;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.wanderingtrader.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffer;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Arrays;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

    public WanderingTraderMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    public void replaceTrades(ServerLevel serverLevel, CallbackInfo ci) {
        TradeTable tradeTable = getTradeTable();
        if (tradeTable != null) {
            // Cancel vanilla trades
            ci.cancel();
            for (int level = 1; level <= tradeTable.maxLevel(); level++) {
                MerchantOffer[] tradeOffers = tradeTable.getTradeOffers(this, level);
                MerchantOffers tradeOfferList = this.getOffers();
                tradeOfferList.addAll(Arrays.asList(tradeOffers));
            }
        }
    }

    private TradeTable getTradeTable() {
        if (this.level() instanceof ServerLevel) {
            TradeManager tradeManager = VillagerConfig.TRADE_MANAGER;
            return tradeManager.getTrade(TradeProvider.WANDERING_TRADER_ID);
        }
        return null;
    }
}
