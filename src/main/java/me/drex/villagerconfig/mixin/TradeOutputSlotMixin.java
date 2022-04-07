package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.config.ConfigEntries;
import me.drex.villagerconfig.util.IMerchantEntity;
import me.drex.villagerconfig.util.Math;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.slot.TradeOutputSlot;
import net.minecraft.village.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(TradeOutputSlot.class)
public abstract class TradeOutputSlotMixin {


    @Shadow
    @Final
    private Merchant merchant;

    @Inject(
            method = "onTakeItem",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/Merchant;trade(Lnet/minecraft/village/TradeOffer;)V"
            )
    )
    public void onTrade(PlayerEntity player, ItemStack stack, CallbackInfo ci) {
        if (ConfigEntries.oldTrades.enabled) {
            if (Math.chance(ConfigEntries.oldTrades.unlockChance)) {
                if (this.merchant instanceof IMerchantEntity merchantEntity) {
                    merchantEntity.enableTrades();
                }
            }
        }
    }

}
