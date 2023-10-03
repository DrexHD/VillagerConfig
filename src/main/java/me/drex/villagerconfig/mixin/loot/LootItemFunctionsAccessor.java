package me.drex.villagerconfig.mixin.loot;

import com.mojang.serialization.Codec;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootItemFunctions.class)
public interface LootItemFunctionsAccessor {

    @Invoker
    static LootItemFunctionType invokeRegister(String id, Codec<? extends LootItemFunction> codec) {
        throw new AssertionError();
    }

}
