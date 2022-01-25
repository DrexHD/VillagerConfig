package me.drex.villagerfix.json.behavior;

import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.json.ValidateAble;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Random;

public class Trade implements TradeOffers.Factory, ValidateAble {

    final TradeItem[] wants;
    final TradeItem[] gives;
    final int trader_exp;
    final int max_uses;
    final boolean reward_exp;

    public Trade(TradeItem[] wants, TradeItem[] gives, int trader_exp, int max_uses, boolean reward_exp) {
        this.wants = wants;
        this.gives = gives;
        this.trader_exp = trader_exp;
        this.max_uses = max_uses;
        this.reward_exp = reward_exp;
    }

    @Nullable
    @Override
    public TradeOffer create(Entity entity, Random random) {
        TradeItem first = wants[0];
        ItemStack firstBuyItem = first.generateItem(entity, random);
        ItemStack secondBuyItem = ItemStack.EMPTY;
        float priceMultiplier = first.price_multiplier;
        if (wants.length > 1) {
            TradeItem second = wants[1];
            secondBuyItem = second.generateItem(entity, random);
        }
        ItemStack sellItem = gives[0].generateItem(entity, random);
        return new TradeOffer(firstBuyItem, secondBuyItem, sellItem, max_uses, trader_exp, priceMultiplier);
    }

    @Override
    public String toString() {
        return "Trade{" +
                "wants=" + Arrays.toString(wants) +
                ", gives=" + Arrays.toString(gives) +
                ", trader_exp=" + trader_exp +
                ", max_uses=" + max_uses +
                ", reward_exp=" + reward_exp +
                '}';
    }

    @Override
    public void validate() {
        if (wants == null) throw new IllegalArgumentException("Wants is not specified");
        if (wants.length == 0) throw new IllegalArgumentException("Wants is empty");
        if (gives == null) throw new IllegalArgumentException("Gives is not specified");
        if (gives.length == 0) throw new IllegalArgumentException("Gives is empty");
        if (gives.length > 1) VillagerFix.LOGGER.warn("Gives can't have more than one value, but has {}: {}", gives.length, Arrays.toString(gives));
        // TODO:
        if (!reward_exp) VillagerFix.LOGGER.warn("Disabling reward exp is currently not supported.");
    }

}
