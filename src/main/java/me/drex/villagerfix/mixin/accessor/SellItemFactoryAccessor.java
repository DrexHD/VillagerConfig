package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellItemFactory.class)
public interface SellItemFactoryAccessor {

    @Accessor("price")
    public int getPrice();

    @Accessor("experience")
    public int getExperience();

    @Accessor("sell")
    public ItemStack getSell();

    @Accessor("multiplier")
    public float getMultiplier();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("count")
    public int getCount();



}
