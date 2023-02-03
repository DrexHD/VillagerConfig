package me.drex.villagerconfig.mixin.loot;

import net.minecraft.loot.provider.number.LootNumberProvider;
import net.minecraft.loot.provider.number.LootNumberProviderType;
import net.minecraft.loot.provider.number.LootNumberProviderTypes;
import net.minecraft.util.JsonSerializer;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootNumberProviderTypes.class)
public interface LootNumberProviderTypesAccessor {

    @Invoker
    static LootNumberProviderType invokeRegister(String id, JsonSerializer<? extends LootNumberProvider> jsonSerializer) {
        throw new AssertionError();
    }

}
