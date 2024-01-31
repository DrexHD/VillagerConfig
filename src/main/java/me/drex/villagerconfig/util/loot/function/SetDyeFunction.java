package me.drex.villagerconfig.util.loot.function;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Sets;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import me.drex.villagerconfig.util.loot.LootItemFunctionTypes;
import net.minecraft.tags.ItemTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.item.DyeColor;
import net.minecraft.world.item.DyeItem;
import net.minecraft.world.item.DyeableLeatherItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.functions.LootItemConditionalFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import org.jetbrains.annotations.NotNull;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public class SetDyeFunction extends LootItemConditionalFunction {

    public static final Codec<SetDyeFunction> CODEC = RecordCodecBuilder.create(
        instance -> commonFields(instance)
            .and(
                instance.group(
                    ExtraCodecs.strictOptionalField(DyeColor.CODEC.listOf(), "dye_colors").forGetter(setDyeFunction -> setDyeFunction.dyeColors),
                    Codec.BOOL.fieldOf("add").orElse(false).forGetter(setDyeFunction -> setDyeFunction.add)
                )
            )
            .apply(instance, SetDyeFunction::new)
    );

    private final Optional<List<DyeColor>> dyeColors;
    private final boolean add;

    protected SetDyeFunction(List<LootItemCondition> conditions, Optional<List<DyeColor>> dyeColors, boolean add) {
        super(conditions);
        this.dyeColors = dyeColors;
        this.add = add;
    }

    @Override
    protected @NotNull ItemStack run(@NotNull ItemStack stack, @NotNull LootContext context) {
        if (stack.is(ItemTags.DYEABLE)) return stack;
        if (!add) {
            DyeableLeatherItem.clearColor(stack);
        }
        List<DyeColor> colors = dyeColors.orElse(ImmutableList.copyOf(DyeColor.values()));
        DyeColor color = colors.get(context.getRandom().nextInt(colors.size()));
        return DyeableLeatherItem.dyeArmor(stack, Collections.singletonList(DyeItem.byColor(color)));
    }

    @Override
    public @NotNull LootItemFunctionType getType() {
        return LootItemFunctionTypes.SET_DYE;
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
            return new SetDyeFunction(this.getConditions(), this.dyeColors.isEmpty() ? Optional.empty() : Optional.of(ImmutableList.copyOf(this.dyeColors)), add);
        }

    }

}
