package me.drex.villagerfix.mixin;

import me.drex.villagerfix.OldTradeOffer;
import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.Helper;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
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

    @Mutable
    @Shadow
    @Final
    private int maxUses;

    @Redirect(method = "increaseSpecialPrice", at = @At(value = "FIELD", target = "Lnet/minecraft/village/TradeOffer;specialPrice:I", opcode = 181))
    public void adjustSpecialPrice(TradeOffer tradeOffer, int increment) {
        int maxDiscount = (int) ((this.firstBuyItem.getCount()) * -(ConfigEntries.features.maxDiscount / 100));
        int maxRaise = (int) ((this.firstBuyItem.getCount()) * +(ConfigEntries.features.maxRaise / 100));
        tradeOffer.setSpecialPrice(MathHelper.clamp(tradeOffer.getSpecialPrice() + increment, maxDiscount, maxRaise));
    }

    @Inject(method = "<init>(Lnet/minecraft/nbt/CompoundTag;)V", at = @At(value = "RETURN"))
    public void fromTag(CompoundTag compoundTag, CallbackInfo ci) {
        if (ConfigEntries.oldTrades.enabled) {
            if (compoundTag.contains("villagerfix_disabled", 1)) {
                this.disabled = compoundTag.getBoolean("disabled");
            }
        }
    }

    @Inject(method = "toTag", at = @At(value = "RETURN"), locals = LocalCapture.CAPTURE_FAILHARD)
    public void toTag(CallbackInfoReturnable<CompoundTag> cir, CompoundTag compoundTag) {
        if (ConfigEntries.oldTrades.enabled) {
            compoundTag.putBoolean("villagerfix_disabled", this.disabled);
        }
    }

    /*
     * Re-implement old trading https://minecraft.gamepedia.com/Trading/Before_Village_%26_Pillage
     * */
    @Inject(method = "use", at = @At(value = "RETURN"))
    public void onUse(CallbackInfo ci) {
        if (ConfigEntries.oldTrades.enabled) {
            if (this.uses > ConfigEntries.oldTrades.minUses) {
                if (Helper.chance(ConfigEntries.oldTrades.lockChance)) {
                    this.disable();
                }
            }
            if (this.uses > ConfigEntries.oldTrades.maxUses - 1) {
                this.disable();
            }
        }
    }

    /**
     * @author Drex
     * @reason Re-add 1.12 villager trade reset mechanics
     */
    @Overwrite
    public boolean isDisabled() {
        return ConfigEntries.oldTrades.enabled ? this.disabled : this.uses >= this.maxUses;
    }

    public void enable() {
        this.uses = 0;
        this.disabled = false;
    }

    public void disable() {
        this.disabled = true;
    }

}
