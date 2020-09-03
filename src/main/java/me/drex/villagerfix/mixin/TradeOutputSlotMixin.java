package me.drex.villagerfix.mixin;

import me.drex.villagerfix.OldTradeOffer;
import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.util.Helper;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.Trader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TradeOutputSlot.class)
public class TradeOutputSlotMixin {

    @Shadow
    @Final
    private Trader trader;

    @Inject(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/Trader;trade(Lnet/minecraft/village/TradeOffer;)V"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void onTrade(PlayerEntity player, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (VillagerFix.INSTANCE.config().oldtrades.enabled) {
            if (Helper.chance(VillagerFix.INSTANCE.config().oldtrades.unlockchance)) {
                this.trader.getOffers().forEach(tradeOffer -> {
                    ((OldTradeOffer) tradeOffer).enable();
                });
            }
        }
//        if (player instanceof ServerPlayerEntity && trader instanceof VillagerEntity) {
//            ServerPlayerEntity serverPlayerEntity = (ServerPlayerEntity) player;
//            VillagerEntity villagerEntity = (VillagerEntity) trader;
//            serverPlayerEntity.sendTradeOffers(0, this.trader.getOffers(), villagerEntity.getVillagerData().getLevel(), trader.getExperience(), trader.isLeveledTrader(), trader.canRefreshTrades());
//        }
    }

}
