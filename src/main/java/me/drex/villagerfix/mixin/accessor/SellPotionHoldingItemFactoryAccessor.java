package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellPotionHoldingItemFactory.class)
public interface SellPotionHoldingItemFactoryAccessor {

    @Accessor("price")
    public int getPrice();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("experience")
    public int getExperience();

    @Accessor("sell")
    public ItemStack getSell();

    @Accessor("secondBuy")
    public Item getSecondBuy();

    @Accessor("priceMultiplier")
    public float getPriceMultiplier();


}
