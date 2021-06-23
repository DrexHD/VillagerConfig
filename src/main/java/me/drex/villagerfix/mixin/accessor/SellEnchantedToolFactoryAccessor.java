package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellEnchantedToolFactory.class)
public interface SellEnchantedToolFactoryAccessor {

    @Accessor("tool")
    public ItemStack getTool();

    @Accessor("basePrice")
    public int getBasePrice();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("experience")
    public int getExperience();

    @Accessor("multiplier")
    public float getMultiplier();


}
