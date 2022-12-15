package me.drex.villagerconfig.util;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import me.drex.villagerconfig.VillagerConfig;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.JsonHelper;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;

public class SetDyeFunction extends ConditionalLootFunction {

    final Dye dye;
    final boolean add;

    protected SetDyeFunction(LootCondition[] conditions, Dye dye, boolean add) {
        super(conditions);
        this.dye = dye;
        this.add = add;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (!add && stack.getItem() instanceof DyeableItem dyeableItem) {
            dyeableItem.removeColor(stack);
        }
        return DyeableItem.blendAndSetColor(stack, Collections.singletonList(DyeItem.byColor(dye.getColor(context))));
    }

    @Override
    public LootFunctionType getType() {
        return VillagerConfig.SET_DYE;
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<SetDyeFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetDyeFunction setDyeFunction, JsonSerializationContext context) {
            super.toJson(jsonObject, setDyeFunction, context);
            jsonObject.addProperty("dye", setDyeFunction.dye.serialize(context));
            jsonObject.addProperty("add", setDyeFunction.add);
        }

        @Override
        public SetDyeFunction fromJson(JsonObject jsonObject, JsonDeserializationContext context, LootCondition[] conditions) {
            Dye dye = Dye.deserialize(jsonObject, context);
            boolean add = JsonHelper.getBoolean(jsonObject, "add", true);
            return new SetDyeFunction(conditions, dye, add);
        }
    }

    static class Dye {

        @Nullable
        final DyeColor dyeColor;

        Dye(@Nullable DyeColor dyeColor) {
            this.dyeColor = dyeColor;
        }

        public static Dye random() {
            return new Dye(null);
        }

        public static Dye dyeColor(DyeColor dyeColor) {
            return new Dye(dyeColor);
        }

        DyeColor getColor(LootContext context) {
            if (dyeColor != null) {
                return dyeColor;
            } else {
                return DyeColor.byId(context.getRandom().nextInt(DyeColor.values().length));
            }
        }

        public String serialize(JsonSerializationContext context) {
            if (dyeColor != null) {
                return dyeColor.name();
            } else {
                return "random";
            }
        }

        public static Dye deserialize(JsonObject json, JsonDeserializationContext context) {
            String dye = JsonHelper.getString(json, "dye");
            if (dye.equals("random")) {
                return Dye.random();
            } else {
                DyeColor dyeColor = DyeColor.byName(dye, null);
                if (dyeColor == null) {
                    throw new JsonSyntaxException("Unknown dye: " + dye);
                }
                return Dye.dyeColor(dyeColor);
            }
        }

    }


}
