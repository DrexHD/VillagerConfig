package me.drex.villagerfix.mixin.accessor;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellSuspiciousStewFactory.class)
public interface SellSuspiciousStewFactoryAccessor {

    @Accessor("effect")
    StatusEffect getEffect();

    @Accessor("duration")
    int getDuration();

    @Accessor("experience")
    int getExperience();

    @Accessor("multiplier")
    float getMultiplier();

}
