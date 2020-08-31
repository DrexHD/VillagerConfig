package me.drex.villagerfix.mixin;

import me.drex.villagerfix.VillagerFix;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TradeOffer.class)
public class TradeOfferMixin {

    @Shadow
    private int specialPrice;

    @Shadow
    @Final
    private ItemStack firstBuyItem;

    /**
     * @author Drex
     * @reason Manipulate the villager discount to not be underneath a configurable threshold
     */
    @Overwrite
    public void increaseSpecialPrice(int i) {
        int maxDiscount = (int) ((this.firstBuyItem.getCount()) *- (VillagerFix.INSTANCE.config().maxdiscount / 100));
        int maxRaise = (int) ((this.firstBuyItem.getCount()) *+ (VillagerFix.INSTANCE.config().maxraise / 100));
        this.specialPrice = MathHelper.clamp(this.specialPrice+i, maxDiscount, maxRaise);
    }

}
