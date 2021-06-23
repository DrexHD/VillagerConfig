package me.drex.villagerfix.mixin.accessor;

import net.minecraft.entity.effect.StatusEffect;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellSuspiciousStewFactory.class)
public interface SellSuspiciousStewFactoryAccessor {

    @Accessor("effect")
    public StatusEffect getEffect();

    @Accessor("duration")
    public int getDuration();

    @Accessor("experience")
    public int getExperience();

    @Accessor("multiplier")
    public float getMultiplier();

}
