package me.drex.villagerconfig.common.mixin;

import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.data.TradeTable;
import me.drex.villagerconfig.common.util.TradeManager;
import me.drex.villagerconfig.common.util.TradeProvider;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.entity.npc/*? if > 1.21.10 {*/.wanderingtrader/*?}*/.WanderingTrader;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(WanderingTrader.class)
public abstract class WanderingTraderMixin extends AbstractVillager {

    public WanderingTraderMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(method = "updateTrades", at = @At("HEAD"), cancellable = true)
    public void replaceTrades(/*? if > 1.21.10 {*/ServerLevel serverLevel, /*?}*/CallbackInfo ci) {
        TradeTable tradeTable = getTradeTable();
        if (tradeTable != null) {
            // Cancel vanilla trades
            ci.cancel();
            for (int level = 1; level <= tradeTable.maxLevel(); level++) {
                VillagerTrades.ItemListing[] tradeOffers = tradeTable.getTradeOffers(this, level);
                MerchantOffers tradeOfferList = this.getOffers();
                for (VillagerTrades.ItemListing tradeOffer : tradeOffers) {
                    tradeOfferList.add(tradeOffer.getOffer(/*? if > 1.21.10 {*/serverLevel, /*?}*/this, this.random));
                }
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
