package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.Item;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.BuyForOneEmeraldFactory.class)
public interface BuyForOneEmeraldFactoryAccessor {

    @Accessor("buy")
    public Item getBuy();

    @Accessor("price")
    public int getPrice();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("experience")
    public int getExperience();

}
