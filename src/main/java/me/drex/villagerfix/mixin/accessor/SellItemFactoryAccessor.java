package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellItemFactory.class)
public interface SellItemFactoryAccessor {

    @Accessor("price")
    int getPrice();

    @Accessor("experience")
    int getExperience();

    @Accessor("sell")
    ItemStack getSell();

    @Accessor("multiplier")
    float getMultiplier();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("count")
    int getCount();

}
