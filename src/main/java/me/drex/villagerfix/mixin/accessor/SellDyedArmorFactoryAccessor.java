package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.Item;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellDyedArmorFactory.class)
public interface SellDyedArmorFactoryAccessor {

    @Accessor("price")
    public int getPrice();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("experience")
    public int getExperience();

    @Accessor("sell")
    public Item getSell();

}
