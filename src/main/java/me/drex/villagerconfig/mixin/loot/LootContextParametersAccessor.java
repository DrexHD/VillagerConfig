package me.drex.villagerconfig.mixin.loot;

import net.minecraft.loot.context.LootContextParameter;
import net.minecraft.loot.context.LootContextParameters;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootContextParameters.class)
public interface LootContextParametersAccessor {

    @Invoker
    static <T> LootContextParameter<T> invokeRegister(String name) {
        throw new AssertionError();
    }

}
