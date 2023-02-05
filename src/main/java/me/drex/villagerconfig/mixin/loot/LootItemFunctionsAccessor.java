package me.drex.villagerconfig.mixin.loot;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootItemFunctions.class)
public interface LootItemFunctionsAccessor {

    @Invoker
    static LootItemFunctionType invokeRegister(String id, Serializer<? extends LootItemFunction> jsonSerializer) {
        throw new AssertionError();
    }

}
