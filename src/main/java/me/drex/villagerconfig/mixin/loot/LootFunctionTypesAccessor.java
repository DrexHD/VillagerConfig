package me.drex.villagerconfig.mixin.loot;

import net.minecraft.loot.function.LootFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.JsonSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootFunctionTypes.class)
public interface LootFunctionTypesAccessor {

    @Invoker
    static LootFunctionType invokeRegister(String id, JsonSerializer<? extends LootFunction> jsonSerializer) {
        throw new AssertionError();
    }

}
