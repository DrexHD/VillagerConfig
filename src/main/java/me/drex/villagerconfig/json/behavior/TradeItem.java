package me.drex.villagerconfig.json.behavior;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.TradeTableReporter;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.loot.provider.number.ConstantLootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.server.world.ServerWorld;

import java.util.Arrays;
import java.util.Random;
import java.util.function.BiFunction;

public class TradeItem implements IValidate {

    Item item;
    final TradeItem[] choice;
    LootNumberProvider quantity;
    public LootNumberProvider price_multiplier;
    final LootFunction[] functions;

    public TradeItem(Item item, TradeItem[] choice, LootNumberProvider quantity, LootNumberProvider price_multiplier, LootFunction[] functions) {
        this.item = item;
        this.choice = choice;
        this.quantity = quantity;
        this.price_multiplier = price_multiplier;
        this.functions = functions;
    }

    public ItemStack generateItem(Entity entity, Random random) {
        if (choice != null) {
            TradeItem item = choice[random.nextInt(choice.length)];
            return item.generateItem(entity, random);
        } else {
            LootContext.Builder builder = new LootContext.Builder((ServerWorld) entity.world).random(random).parameter(LootContextParameters.THIS_ENTITY, entity).parameter(LootContextParameters.ORIGIN, entity.getPos());
            LootContext lootContext = builder.build(VillagerConfig.VILLAGER_LOOT_CONTEXT);
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
                if (this.price_multiplier != null) {
                    reporter.warn("choice[] detected, ignoring price_multiplier");
                }
                if (this.functions != null) {
                    reporter.warn("choice[] detected, ignoring functions");
                }
            }
        } else {
            // Default values
            this.quantity = this.quantity != null ? this.quantity : ConstantLootNumberProvider.create(1);
            this.price_multiplier = this.price_multiplier != null ? this.price_multiplier : ConstantLootNumberProvider.create(0.20F);
            this.item = this.item != null ? this.item : Items.AIR;
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
