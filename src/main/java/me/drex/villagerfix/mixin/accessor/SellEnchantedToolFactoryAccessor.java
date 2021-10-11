package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public interface SellEnchantedToolFactoryAccessor {

    @Accessor("tool")
    ItemStack getTool();

    @Accessor("basePrice")
    int getBasePrice();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("experience")
    int getExperience();

    @Accessor("multiplier")
    float getMultiplier();

}
