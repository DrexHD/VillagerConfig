package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.ProcessItemFactory.class)
public interface ProccessItemFactoryAccessor {

    @Accessor("price")
    public int getPrice();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("experience")
    public int getExperience();

    @Accessor("secondBuy")
    public ItemStack getSecondBuy();

    @Accessor("sell")
    public ItemStack getSell();

    @Accessor("secondCount")
    public int getSecondCount();

    @Accessor("sellCount")
    public int getSellCount();

    @Accessor("multiplier")
    public float getMultiplier();

}
