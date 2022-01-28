package me.drex.villagerconfig.json.behavior;

import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemStack;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOffers;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Random;

public class BehaviorTrade implements TradeOffers.Factory {

    final TradeItem[] wants;
    final TradeItem[] gives;
    final int trader_exp;
    final int max_uses;
    final boolean reward_exp;

    public BehaviorTrade(TradeItem[] wants, TradeItem[] gives, int trader_exp, int max_uses, boolean reward_exp) {
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

    protected void validate(TradeTableReporter reporter) {
        if (wants == null) {
            reporter.error("Missing wants[]");
        } else if (wants.length == 0) {
            reporter.error("wants[] is empty");
        } else {
            if (wants.length > 2) {
                reporter.warn("wants[] contains more than two entries");
            }
            if (gives == null) {
                reporter.error("Missing gives[]");
            } else {
                if (gives.length == 0) {
                    reporter.error("gives[] is empty");
                } else {
                    if (gives.length > 1) {
                        reporter.warn("gives[] contains more than one entry");
                    }
                    for (int i = 0; i < wants.length; i++) {
                        wants[i].validate(reporter.makeChild(".wants[" + i + "]"));
                    }
                    for (int i = 0; i < gives.length; i++) {
                        gives[i].validate(reporter.makeChild(".gives[" + i + "]"));
                    }
                }
            }
        }
    }

}
