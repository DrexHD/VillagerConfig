package me.drex.villagerconfig.common.mixin;

import net.minecraft.world.item.trading.MerchantOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(MerchantOffer.class)
public interface MerchantOfferAccessor {

    @Accessor
    @Mutable
    void setRewardExp(boolean rewardExp);

}
