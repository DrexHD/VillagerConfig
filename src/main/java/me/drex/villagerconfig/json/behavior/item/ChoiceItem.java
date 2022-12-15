package me.drex.villagerconfig.json.behavior.item;

import net.minecraft.item.Item;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.provider.number.LootNumberProvider;

public class ChoiceItem extends TradeItem {

    private final LootCondition[] conditions;

    public ChoiceItem(Item item) {
        super(item);
        this.conditions = null;
    }

    public ChoiceItem(Item item, ChoiceItem[] choice, LootNumberProvider quantity, LootFunction[] functions, LootCondition[] conditions) {
        super(item, choice, quantity, functions);
        this.conditions = conditions;
    }

    public LootCondition[] conditions() {
        return this.conditions;
    }

}
