package me.drex.villagerconfig.util.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.gson.*;
import me.drex.villagerconfig.util.loot.LootItemFunctionTypes;
import net.minecraft.util.GsonHelper;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class SetDyeFunction extends LootItemConditionalFunction {

    private final List<DyeColor> dyeColors;
    private final boolean add;

    protected SetDyeFunction(LootItemCondition[] conditions, Collection<DyeColor> dyeColors, boolean add) {
        super(conditions);
        this.dyeColors = ImmutableList.copyOf(dyeColors);
        this.add = add;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
        if (!add && stack.getItem() instanceof DyeableLeatherItem dyeableItem) {
            dyeableItem.clearColor(stack);
        }
        List<DyeColor> colors;
        if (!dyeColors.isEmpty()) {
            colors = dyeColors;
        } else {
            colors = ImmutableList.copyOf(DyeColor.values());
        }
        DyeColor color = colors.get(context.getRandom().nextInt(colors.size()));
        return DyeableLeatherItem.dyeArmor(stack, Collections.singletonList(DyeItem.byColor(color)));
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return LootItemFunctionTypes.SET_DYE;
    }

    public static class Serializer extends LootItemConditionalFunction.Serializer<SetDyeFunction> {

        @Override
        public void serialize(@NotNull JsonObject jsonObject, @NotNull SetDyeFunction setDyeFunction, @NotNull JsonSerializationContext context) {
            super.serialize(jsonObject, setDyeFunction, context);
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
        public @NotNull SetDyeFunction deserialize(JsonObject jsonObject, @NotNull JsonDeserializationContext context, LootItemCondition @NotNull [] conditions) {
            ArrayList<DyeColor> dyeColors = Lists.newArrayList();
            if (jsonObject.has("dye_colors")) {
                JsonArray jsonArray = GsonHelper.getAsJsonArray(jsonObject, "dye_colors");
                for (JsonElement jsonElement : jsonArray) {
                    String dyeId = GsonHelper.convertToString(jsonElement, "dye_color");
                    DyeColor dyeColor = DyeColor.byName(dyeId, null);
                    if (dyeColor == null) throw new JsonSyntaxException("Unknown dye color '" + dyeId + "'");
                    dyeColors.add(dyeColor);
                }
            }
            boolean add = GsonHelper.getAsBoolean(jsonObject, "add", false);
            return new SetDyeFunction(conditions, dyeColors, add);
        }
    }

    public static class Builder
            extends LootItemConditionalFunction.Builder<SetDyeFunction.Builder> {
        private final Set<DyeColor> dyeColors = Sets.newHashSet();
        private final boolean add;

        public Builder() {
            this(false);
        }

        public Builder(boolean add) {
            this.add = add;
        }

        @Override
        protected @NotNull Builder getThis() {
            return this;
        }

        public Builder add(DyeColor dyeColor) {
            this.dyeColors.add(dyeColor);
            return this;
        }

        @Override
        public @NotNull SetDyeFunction build() {
            return new SetDyeFunction(this.getConditions(), this.dyeColors, add);
        }

    }


}
