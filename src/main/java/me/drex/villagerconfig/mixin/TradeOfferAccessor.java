package me.drex.villagerconfig.mixin;

import net.minecraft.village.TradeOffer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffer.class)
public interface TradeOfferAccessor {

    @Accessor
    void setRewardingPlayerExperience(boolean rewardingPlayerExperience);

}
