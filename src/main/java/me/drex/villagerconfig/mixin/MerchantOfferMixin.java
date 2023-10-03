package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.RandomUtil;
import me.drex.villagerconfig.util.interfaces.IMerchantOffer;
import me.drex.villagerconfig.util.interfaces.IVillager;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.npc.AbstractVillager;
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
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

@Mixin(MerchantOffer.class)
public abstract class MerchantOfferMixin implements IMerchantOffer {

    public boolean disabled = false;
    private AbstractVillager merchantEntity = null;

    @Shadow
    @Final
    private ItemStack baseCostA;

    @Shadow
    private int uses;

    @Override
    public void onUse(AbstractVillager merchant) {
        this.merchantEntity = merchant;
    }

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
            method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V",
            at = @At("RETURN")
    )
    public void readCustomTags(CompoundTag nbt, CallbackInfo ci) {
        if (CONFIG.oldTrades.enabled) {
            if (nbt.contains("villagerconfig_disabled", 1)) {
                this.disabled = nbt.getBoolean("villagerconfig_disabled");
            }
        }
    }

    @Inject(
            method = "createTag",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void writeCustomTags(CallbackInfoReturnable<CompoundTag> cir, CompoundTag nbt) {
        if (CONFIG.oldTrades.enabled) {
            nbt.putBoolean("villagerconfig_disabled", this.disabled);
        }
    }

    /*
     * Re-implement old trading https://minecraft.gamepedia.com/Trading/Before_Village_%26_Pillage
     * */
    @Inject(
            method = "increaseUses",
            at = @At("RETURN")
    )
    public void onUse(CallbackInfo ci) {
        if (CONFIG.oldTrades.enabled) {
            if (this.uses > CONFIG.oldTrades.minUses) {
                if (RandomUtil.chance(CONFIG.oldTrades.lockChance)) {
                    this.disable();
                }
            }
            if (this.uses > CONFIG.oldTrades.maxUses - 1) {
                this.disable();
            }
        }
    }

    @Inject(
            method = "isOutOfStock",
            at = @At("HEAD"),
            cancellable = true
    )
    public void addOldTradeMechanics(CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.features.infiniteTrades) cir.setReturnValue(false);
        if (CONFIG.oldTrades.enabled) cir.setReturnValue(this.disabled);
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

    public void enable() {
        this.uses = 0;
        this.disabled = false;
    }

    public void disable() {
        if (merchantEntity != null) ((IVillager) merchantEntity).updateCustomOffers();
        this.disabled = true;
    }

}
