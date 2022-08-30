package me.drex.villagerconfig.json.behavior.item;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.json.behavior.IValidate;
import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.condition.LootConditionTypes;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.math.random.Random;

import javax.annotation.Nullable;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Stream;

public class TradeItem implements IValidate {

    Item item;
    final ChoiceItem[] choice;
    LootNumberProvider quantity;
    final LootFunction[] functions;

    public TradeItem(Item item) {
        this(item, ConstantLootNumberProvider.create(1));
    }

    public TradeItem(Item item, LootNumberProvider quantity) {
        this(item, null, quantity, null);
    }

    public TradeItem(Item item, ChoiceItem[] choice, LootNumberProvider quantity, @Nullable LootFunction[] functions) {
        this.item = item;
        this.choice = choice;
        this.quantity = quantity;
        this.functions = functions;
    }

    public ItemStack generateItem(Entity entity, Random random) {
        LootContext.Builder builder = new LootContext.Builder((ServerWorld) entity.world).random(random).parameter(LootContextParameters.THIS_ENTITY, entity).parameter(LootContextParameters.ORIGIN, entity.getPos());
        LootContext lootContext = builder.build(VillagerConfig.VILLAGER_LOOT_CONTEXT);
        if (choice != null) {
            List<ChoiceItem> list = Arrays.asList(choice);
            // Shuffle
            for (int i = list.size(); i > 1; i--)
                Collections.swap(list, i - 1, random.nextInt(i));
            Stream<ChoiceItem> stream = list.stream().dropWhile(choiceItem -> LootConditionTypes.joinAnd(choiceItem.conditions()).negate().test(lootContext));
            return stream.findFirst().orElse(new ChoiceItem(Items.AIR)).generateItem(entity, random);
        } else {
            ItemStack itemStack = new ItemStack(item, quantity.nextInt(lootContext));
            if (functions != null) {
                BiFunction<ItemStack, LootContext, ItemStack> combinedFunction = LootFunctionTypes.join(functions);
                return combinedFunction.apply(itemStack, lootContext);
            }
            return itemStack;
        }
    }

    public void validate(TradeTableReporter reporter) {
        if (choice != null) {
            if (choice.length == 0) {
                reporter.error("choice[] is empty");
            } else {
                if (this.item != null) {
                    reporter.warn("choice[] detected, ignoring item");
                }
                if (this.quantity != null) {
                    reporter.warn("choice[] detected, ignoring quantity");
                }
                if (this.functions != null) {
                    reporter.warn("choice[] detected, ignoring functions");
                }
            }
        } else {
            // Default values
            this.quantity = this.quantity != null ? this.quantity : ConstantLootNumberProvider.create(1);
            this.item = this.item != null ? this.item : Items.AIR;
        }
    }

    @Override
    public String toString() {
        return "TradeItem{" +
                "item=" + item +
                ", choice=" + Arrays.toString(choice) +
                ", quantity=" + quantity +
                ", functions=" + Arrays.toString(functions) +
                '}';
    }

}
