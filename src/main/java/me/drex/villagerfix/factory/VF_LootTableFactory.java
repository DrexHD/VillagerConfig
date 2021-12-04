package me.drex.villagerfix.factory;

import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public final class VF_LootTableFactory implements TradeOffers.Factory {
    private LootTable firstBuy;
    private LootTable secondBuy;
    private LootTable sell;
    private int maxUses;
    private int experience;
    private float priceMultiplier;

    public VF_LootTableFactory(LootTable firstBuy, LootTable secondBuy, LootTable sell, int maxUses, int experience, float priceMultiplier) {
        this.firstBuy = firstBuy;
        this.secondBuy = secondBuy;
        this.sell = sell;
        this.maxUses = maxUses;
        this.experience = experience;
        this.priceMultiplier = priceMultiplier;
    }

    @Override
    public @NotNull TradeOffer create(Entity entity, Random random) {
        LootContext.Builder builder = new LootContext.Builder((ServerWorld) entity.world).random(random).parameter(LootContextParameters.THIS_ENTITY, entity);
        List<ItemStack> firstBuyLootTable = firstBuy.generateLoot(builder.build(LootContextTypes.BARTER));
        List<ItemStack> secondBuyLootTable = secondBuy.generateLoot(builder.build(LootContextTypes.BARTER));
        List<ItemStack> sellLootTable = sell.generateLoot(builder.build(LootContextTypes.BARTER));
        return new TradeOffer(this.getItem(firstBuyLootTable), this.getItem(secondBuyLootTable), this.getItem(sellLootTable), maxUses, experience, priceMultiplier);
    }

    private ItemStack getItem(List<ItemStack> itemStacks) {
        if (itemStacks.isEmpty()) return ItemStack.EMPTY;
        return itemStacks.get(0);
    }

}
