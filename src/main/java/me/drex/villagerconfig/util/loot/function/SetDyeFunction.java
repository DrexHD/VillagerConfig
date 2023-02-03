package me.drex.villagerconfig.util.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import me.drex.villagerconfig.util.loot.LootFunctionTypes;
import net.minecraft.item.DyeItem;
import net.minecraft.item.DyeableItem;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.function.ConditionalLootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.util.DyeColor;
import net.minecraft.util.JsonHelper;

import java.util.*;

public class SetDyeFunction extends ConditionalLootFunction {

    private final List<DyeColor> dyeColors;
    private final boolean add;

    protected SetDyeFunction(LootCondition[] conditions, Collection<DyeColor> dyeColors, boolean add) {
        super(conditions);
        this.dyeColors = ImmutableList.copyOf(dyeColors);
        this.add = add;
    }

    @Override
    protected ItemStack process(ItemStack stack, LootContext context) {
        if (!add && stack.getItem() instanceof DyeableItem dyeableItem) {
            dyeableItem.removeColor(stack);
        }
        List<DyeColor> colors;
        if (dyeColors.isEmpty()) {
            colors = dyeColors;
        } else {
            colors = ImmutableList.copyOf(DyeColor.values());
        }
        DyeColor color = colors.get(context.getRandom().nextInt(colors.size()));
        return DyeableItem.blendAndSetColor(stack, Collections.singletonList(DyeItem.byColor(color)));
    }

    @Override
    public LootFunctionType getType() {
        return LootFunctionTypes.SET_DYE;
    }

    public static class Serializer extends ConditionalLootFunction.Serializer<SetDyeFunction> {
        @Override
        public void toJson(JsonObject jsonObject, SetDyeFunction setDyeFunction, JsonSerializationContext context) {
            super.toJson(jsonObject, setDyeFunction, context);
            if (!setDyeFunction.dyeColors.isEmpty()) {
                JsonArray jsonArray = new JsonArray();
                for (DyeColor dyeColor : setDyeFunction.dyeColors) {
                    jsonArray.add(new JsonPrimitive(dyeColor.name()));
                }
                jsonObject.add("dye_colors", jsonArray);
            }
            jsonObject.addProperty("add", setDyeFunction.add);
        }

        @Override
        public SetDyeFunction fromJson(JsonObject jsonObject, JsonDeserializationContext context, LootCondition[] conditions) {
            ArrayList<DyeColor> dyeColors = Lists.newArrayList();
            if (jsonObject.has("dye_colors")) {
                JsonArray jsonArray = JsonHelper.getArray(jsonObject, "dye_colors");
                for (JsonElement jsonElement : jsonArray) {
                    String dyeId = JsonHelper.asString(jsonElement, "dye_color");
                    DyeColor dyeColor = DyeColor.byName(dyeId, null);
                    if (dyeColor == null) throw new JsonSyntaxException("Unknown dye color '" + dyeId + "'");
                    dyeColors.add(dyeColor);
                }
            }
            boolean add = JsonHelper.getBoolean(jsonObject, "add", false);
            return new SetDyeFunction(conditions, dyeColors, add);
        }
    }

    public static class Builder
            extends ConditionalLootFunction.Builder<SetDyeFunction.Builder> {
        private final Set<DyeColor> dyeColors = Sets.newHashSet();
        private final boolean add;

        public Builder() {
            this(false);
        }

        public Builder(boolean add) {
            this.add = add;
        }

        @Override
        protected Builder getThisBuilder() {
            return this;
        }

        public Builder add(DyeColor dyeColor) {
            this.dyeColors.add(dyeColor);
            return this;
        }

        @Override
        public SetDyeFunction build() {
            return new SetDyeFunction(this.getConditions(), this.dyeColors, add);
        }

    }


}
