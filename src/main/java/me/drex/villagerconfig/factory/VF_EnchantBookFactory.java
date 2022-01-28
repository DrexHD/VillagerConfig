package me.drex.villagerconfig.factory;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.entity.Entity;
import net.minecraft.item.EnchantedBookItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

public final class VF_EnchantBookFactory implements TradeOffers.Factory {
    private int maxUses;
    private int experience;
    private float priceMultiplier;
    private boolean hasTreasure;
    private List<Enchantment> blackListedEnchantments;
    private int treasureMultiplier;
    private int basePrice;
    private int levelPrice;
    private int randomBasePrice;
    private int randomLevelPrice;
    private Item firstBuyItem;
    private ItemStack secondBuyItem;

    public VF_EnchantBookFactory(int maxUses, int experience, float priceMultiplier, boolean hasTreasure,
                                 List<Enchantment> blackListedEnchantments,
                                 int treasureMultiplier, int basePrice, int levelPrice, int randomBasePrice,
                                 int randomLevelPrice, Item firstBuyItem,
                                 ItemStack secondBuyItem) {
        this.maxUses = maxUses;
        this.experience = experience;
        this.priceMultiplier = priceMultiplier;
        this.hasTreasure = hasTreasure;
        this.blackListedEnchantments = blackListedEnchantments;
        this.treasureMultiplier = treasureMultiplier;
        this.basePrice = basePrice;
        this.levelPrice = levelPrice;
        this.randomBasePrice = randomBasePrice;
        this.randomLevelPrice = randomLevelPrice;
        this.firstBuyItem = firstBuyItem;
        this.secondBuyItem = secondBuyItem;
    }

    @Override
    public @NotNull TradeOffer create(Entity entity, Random random) {
        List<Enchantment> list = Registry.ENCHANTMENT.stream().filter(enchantment -> enchantment.isAvailableForEnchantedBookOffer() && (this.hasTreasure || !enchantment.isTreasure()) && !blackListedEnchantments.contains(enchantment)).collect(Collectors.toList());
        if (list.isEmpty()) {
            return new TradeOffer(new ItemStack(Items.BARRIER), new ItemStack(Items.BARRIER), new ItemStack(Items.BARRIER), this.maxUses, this.experience, this.priceMultiplier);
        } else {
            Enchantment enchantment = list.get(random.nextInt(list.size()));
            int level = MathHelper.nextInt(random, enchantment.getMinLevel(), enchantment.getMaxLevel());
            ItemStack itemStack = EnchantedBookItem.forEnchantment(new EnchantmentLevelEntry(enchantment, level));
            int priceCount = this.basePrice + random.nextInt(this.randomBasePrice + level * this.randomLevelPrice) + this.levelPrice * level;
            if (enchantment.isTreasure()) {
                priceCount *= this.treasureMultiplier;
            }
            return new TradeOffer(new ItemStack(this.firstBuyItem, Math.min(priceCount, this.firstBuyItem.getMaxCount())), this.secondBuyItem, itemStack, this.maxUses, this.experience, this.priceMultiplier);
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (VF_EnchantBookFactory) obj;
        return this.maxUses == that.maxUses &&
                this.experience == that.experience &&
                Float.floatToIntBits(this.priceMultiplier) == Float.floatToIntBits(that.priceMultiplier) &&
                this.hasTreasure == that.hasTreasure &&
                Objects.equals(this.blackListedEnchantments, that.blackListedEnchantments) &&
                this.treasureMultiplier == that.treasureMultiplier &&
                this.basePrice == that.basePrice &&
                this.levelPrice == that.levelPrice &&
                this.randomBasePrice == that.randomBasePrice &&
                this.randomLevelPrice == that.randomLevelPrice &&
                Objects.equals(this.firstBuyItem, that.firstBuyItem) &&
                Objects.equals(this.secondBuyItem, that.secondBuyItem);
    }

    @Override
    public int hashCode() {
        return Objects.hash(maxUses, experience, priceMultiplier, hasTreasure, blackListedEnchantments, treasureMultiplier, basePrice, levelPrice, randomBasePrice, randomLevelPrice, firstBuyItem, secondBuyItem);
    }

    @Override
    public String toString() {
        return "VF_EnchantBookFactory[" +
                "maxUses=" + maxUses + ", " +
                "experience=" + experience + ", " +
                "priceMultiplier=" + priceMultiplier + ", " +
                "hasTreasure=" + hasTreasure + ", " +
                "blackListedEnchantments=" + blackListedEnchantments + ", " +
                "treasureMultiplier=" + treasureMultiplier + ", " +
                "basePrice=" + basePrice + ", " +
                "levelPrice=" + levelPrice + ", " +
                "randomBasePrice=" + randomBasePrice + ", " +
                "randomLevelPrice=" + randomLevelPrice + ", " +
                "firstBuyItem=" + firstBuyItem + ", " +
                "secondBuyItem=" + secondBuyItem + ']';
    }


}