package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.Math;
import me.drex.villagerconfig.util.interfaces.IVillager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantResultSlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.Merchant;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

@Mixin(MerchantResultSlot.class)
public abstract class MerchantResultSlotMixin {

    @Shadow
    @Final
    private Merchant merchant;

    @Inject(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/item/trading/Merchant;notifyTrade(Lnet/minecraft/world/item/trading/MerchantOffer;)V"
            )
    )
    public void onTrade(Player player, ItemStack stack, CallbackInfo ci) {
        if (CONFIG.oldTrades.enabled) {
            if (Math.chance(CONFIG.oldTrades.unlockChance)) {
                if (this.merchant instanceof IVillager merchantEntity) {
                    merchantEntity.enableTrades();
                }
            }
        }
    }

}
