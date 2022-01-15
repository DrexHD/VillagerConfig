package me.drex.villagerfix.mixin;

import me.drex.villagerfix.util.OldTradeOffer;
import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.Math;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

@Mixin(TradeOffer.class)
public abstract class TradeOfferMixin implements OldTradeOffer {

    public boolean disabled = false;

    @Shadow
    @Final
    private ItemStack firstBuyItem;

    @Shadow
    private int uses;

    @Redirect(
            method = "increaseSpecialPrice",
            at = @At(
                    value = "FIELD",
                    target = "Lnet/minecraft/village/TradeOffer;specialPrice:I",
                    opcode = Opcodes.PUTFIELD
            )
    )
    public void adjustSpecialPrice(TradeOffer tradeOffer, int increment) {
        int maxDiscount = (int) ((this.firstBuyItem.getCount()) * -(ConfigEntries.features.maxDiscount / 100));
        int maxRaise = (int) ((this.firstBuyItem.getCount()) * +(ConfigEntries.features.maxRaise / 100));
        tradeOffer.setSpecialPrice(MathHelper.clamp(tradeOffer.getSpecialPrice() + increment, maxDiscount, maxRaise));
    }

    @Inject(
            method = "<init>(Lnet/minecraft/nbt/NbtCompound;)V",
            at = @At("RETURN")
    )
    public void readCustomTags(NbtCompound nbt, CallbackInfo ci) {
        if (ConfigEntries.oldTrades.enabled) {
            if (nbt.contains("villagerfix_disabled", 1)) {
                this.disabled = nbt.getBoolean("disabled");
            }
        }
    }

    @Inject(
            method = "toNbt",
            at = @At("RETURN"),
            locals = LocalCapture.CAPTURE_FAILHARD
    )
    public void writeCustomTags(CallbackInfoReturnable<NbtCompound> cir, NbtCompound nbt) {
        if (ConfigEntries.oldTrades.enabled) {
            nbt.putBoolean("villagerfix_disabled", this.disabled);
        }
    }

    /*
     * Re-implement old trading https://minecraft.gamepedia.com/Trading/Before_Village_%26_Pillage
     * */
    @Inject(
            method = "use",
            at = @At("RETURN")
    )
    public void onUse(CallbackInfo ci) {
        if (ConfigEntries.oldTrades.enabled) {
            if (this.uses > ConfigEntries.oldTrades.minUses) {
                if (Math.chance(ConfigEntries.oldTrades.lockChance)) {
                    this.disable();
                }
            }
            if (this.uses > ConfigEntries.oldTrades.maxUses - 1) {
                this.disable();
            }
        }
    }

    @Inject(
            method = "isDisabled",
            at = @At("HEAD"),
            cancellable = true
    )
    public void addOldTradeMechanics(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigEntries.oldTrades.enabled) cir.setReturnValue(this.disabled);
    }

    public void enable() {
        this.uses = 0;
        this.disabled = false;
    }

    public void disable() {
        this.disabled = true;
    }

}
