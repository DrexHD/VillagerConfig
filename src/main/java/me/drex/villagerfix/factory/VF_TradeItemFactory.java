package me.drex.villagerfix.factory;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.Random;

public final class VF_TradeItemFactory implements TradeOffers.Factory {
    private ItemStack firstBuy;
    private ItemStack secondBuy;
    private ItemStack sell;
    private int maxUses;
    private int experience;
    private float priceMultiplier;

    public VF_TradeItemFactory(ItemStack firstBuy, ItemStack secondBuy, ItemStack sell, int maxUses, int experience, float priceMultiplier) {
        this.firstBuy = firstBuy;
        this.secondBuy = secondBuy;
        this.sell = sell;
        this.maxUses = maxUses;
        this.experience = experience;
        this.priceMultiplier = priceMultiplier;
    }

    @Override
    public @NotNull TradeOffer create(Entity entity, Random random) {
        return new TradeOffer(firstBuy, secondBuy, sell, maxUses, experience, priceMultiplier);
    }

}
