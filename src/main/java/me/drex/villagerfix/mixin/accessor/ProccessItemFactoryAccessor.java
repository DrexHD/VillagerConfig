package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.ProcessItemFactory.class)
public interface ProccessItemFactoryAccessor {

    @Accessor("price")
    int getPrice();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("experience")
    int getExperience();

    @Accessor("secondBuy")
    ItemStack getSecondBuy();

    @Accessor("sell")
    ItemStack getSell();

    @Accessor("secondCount")
    int getSecondCount();

    @Accessor("sellCount")
    int getSellCount();

    @Accessor("multiplier")
    float getMultiplier();

}
