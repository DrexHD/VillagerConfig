package me.drex.villagerconfig.json.behavior.item;

import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.item.Item;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;

import javax.annotation.Nullable;

public class WantItem extends TradeItem {

    private LootNumberProvider price_multiplier;

    public WantItem(Item item, ChoiceItem[] choice, LootNumberProvider quantity, LootNumberProvider price_multiplier, @Nullable LootFunction[] functions) {
        super(item, choice, quantity, functions);
        this.price_multiplier = price_multiplier;
    }

    public LootNumberProvider getPriceMultiplier() {
        return this.price_multiplier;
    }

    @Override
    public void validate(TradeTableReporter reporter) {
        this.price_multiplier = this.price_multiplier != null ? this.price_multiplier : ConstantLootNumberProvider.create(0.20F);
    }
}
