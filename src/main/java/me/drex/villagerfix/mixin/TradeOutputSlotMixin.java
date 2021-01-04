package me.drex.villagerfix.mixin;

import me.drex.villagerfix.OldTradeOffer;
import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.Helper;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.village.Trader;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(TradeOutputSlot.class)
public class TradeOutputSlotMixin {


    @Shadow @Final private Trader trader;

    @Inject(method = "onTakeItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/village/Trader;trade(Lnet/minecraft/village/TradeOffer;)V"))
    public void onTrade(PlayerEntity player, ItemStack stack, CallbackInfoReturnable<ItemStack> cir) {
        if (ConfigEntries.oldTrades.enabled) {
            if (Helper.chance(ConfigEntries.oldTrades.unlockChance)) {
                this.trader.getOffers().forEach(tradeOffer -> {
                    ((OldTradeOffer) tradeOffer).enable();
                });
            }
        }
    }

}
