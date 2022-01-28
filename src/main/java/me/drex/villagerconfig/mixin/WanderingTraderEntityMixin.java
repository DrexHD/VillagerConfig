package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.json.behavior.TradeTable;
import me.drex.villagerconfig.util.IMinecraftServer;
import me.drex.villagerconfig.util.TradeManager;
import me.drex.villagerconfig.util.TradeProvider;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.WanderingTraderEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
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
                    target = "Lnet/minecraft/entity/passive/WanderingTraderEntity;fillRecipesFromPool(Lnet/minecraft/village/TradeOfferList;[Lnet/minecraft/village/TradeOffers$Factory;I)V"
            )
    )
    public void putCustomTradesLevel1(WanderingTraderEntity wanderingTraderEntity, TradeOfferList tradeOffers, TradeOffers.Factory[] factories, int count) {
        TradeTable tradeTable = getTradeTable();
        if (tradeTable != null) {
            TradeOffers.Factory[] customOffers = tradeTable.getTradeOffers(1, this.random);
            this.fillRecipesFromPool(tradeOffers, customOffers, customOffers.length);
            return;
        }
        this.fillRecipesFromPool(tradeOffers, factories, count);
    }

    @Redirect(
            method = "fillRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/TradeOfferList;add(Ljava/lang/Object;)Z"
            )
    )
    public boolean putCustomTradesLevel2(TradeOfferList tradeOffers, Object tradeOffer) {
        TradeTable tradeTable = getTradeTable();
        if (tradeTable != null) {
            TradeOffers.Factory[] customOffers = tradeTable.getTradeOffers(2, this.random);
            this.fillRecipesFromPool(tradeOffers, customOffers, customOffers.length);
            return true;
        }
        return tradeOffers.add((TradeOffer) tradeOffer);
    }

    private TradeTable getTradeTable() {
        if (this.world instanceof ServerWorld serverWorld) {
            TradeManager tradeManager = ((IMinecraftServer) serverWorld.getServer()).getTradeManager();
            return tradeManager.getTrade(TradeProvider.WANDERING_TRADER_ID);
        }
        return null;
    }
}
