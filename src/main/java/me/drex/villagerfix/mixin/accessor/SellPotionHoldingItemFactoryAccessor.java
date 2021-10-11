package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellPotionHoldingItemFactory.class)
public interface SellPotionHoldingItemFactoryAccessor {

    @Accessor("price")
    int getPrice();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("experience")
    int getExperience();

    @Accessor("sell")
    ItemStack getSell();

    @Accessor("secondBuy")
    Item getSecondBuy();

    @Accessor("priceMultiplier")
    float getPriceMultiplier();

}
