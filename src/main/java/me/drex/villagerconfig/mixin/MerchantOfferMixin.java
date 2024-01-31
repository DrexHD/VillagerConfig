package me.drex.villagerconfig.mixin;

import com.mojang.serialization.Codec;
import net.minecraft.util.Mth;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.trading.MerchantOffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin {

    @Shadow
    @Final
    private ItemStack baseCostA;

    @Shadow @Final public static Codec<MerchantOffer> CODEC;

    @Redirect(
            method = "addToSpecialPriceDiff",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/world/item/trading/MerchantOffer;specialPriceDiff:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    public void adjustSpecialPrice(MerchantOffer tradeOffer, int increment) {
        int maxDiscount = (int) ((this.baseCostA.getCount()) * -(CONFIG.features.maxDiscount / 100));
        int maxRaise = (int) ((this.baseCostA.getCount()) * (CONFIG.features.maxRaise / 100));
        tradeOffer.setSpecialPriceDiff(Mth.clamp(tradeOffer.getSpecialPriceDiff() + increment, maxDiscount, maxRaise));
    }

    @Inject(
            method = "isOutOfStock",
            at = @At("HEAD"),
            cancellable = true
    )
    public void addOldTradeMechanics(CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.features.infiniteTrades) cir.setReturnValue(false);
    }

    @Inject(
            method = "getMaxUses",
            at = @At("HEAD"),
            cancellable = true
    )
    public void infiniteUses(CallbackInfoReturnable<Integer> cir) {
        if (CONFIG.features.infiniteTrades) cir.setReturnValue(Integer.MAX_VALUE);
    }

    @Inject(
            method = "updateDemand",
            at = @At("HEAD"),
            cancellable = true
    )
    public void noDemand(CallbackInfo ci) {
        if (CONFIG.features.infiniteTrades) ci.cancel();
    }

}
