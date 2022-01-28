package me.drex.villagerconfig.json.behavior;

import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.server.world.ServerWorld;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;

public class TradeItem {

    final Item item;
    final TradeItem[] choice;
    Integer quantity;
    Float price_multiplier;
    final LootFunction[] functions;

    public TradeItem(Item item, TradeItem[] choice, Integer quantity, Float price_multiplier, LootFunction[] functions) {
        this.item = item;
        this.choice = choice;
        this.quantity = quantity;
        this.price_multiplier = price_multiplier;
        this.functions = functions;
    }

    protected ItemStack generateItem(Entity entity, Random random) {
        if (choice != null) {
            TradeItem item = choice[random.nextInt(choice.length)];
            return item.generateItem(entity, random);
        } else {
            ItemStack itemStack = new ItemStack(item, quantity);
            if (functions != null) {
                BiFunction<ItemStack, LootContext, ItemStack> combinedFunction = LootFunctionTypes.join(functions);
                LootContext.Builder builder = new LootContext.Builder((ServerWorld) entity.world).random(random).parameter(LootContextParameters.THIS_ENTITY, entity);
                return combinedFunction.apply(itemStack, builder.build(LootContextTypes.BARTER));
            }
            return itemStack;
        }
    }

    protected void validate(TradeTableReporter reporter) {
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
                if (this.price_multiplier != null) {
                    reporter.warn("choice[] detected, ignoring price_multiplier");
                }
                if (this.functions != null) {
                    reporter.warn("choice[] detected, ignoring functions");
                }
            }
        } else {
            // Default values
            this.quantity = this.quantity != null ? this.quantity : 1;
            this.price_multiplier = this.price_multiplier != null ? this.price_multiplier : 0.20F;
        }
    }

    @Override
    public String toString() {
        return "TradeItem{" +
                "item=" + item +
                ", choice=" + Arrays.toString(choice) +
                ", quantity=" + quantity +
                ", price_multiplier=" + price_multiplier +
                ", functions=" + Arrays.toString(functions) +
                '}';
    }

}
