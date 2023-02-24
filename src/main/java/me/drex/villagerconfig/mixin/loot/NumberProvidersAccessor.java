package me.drex.villagerconfig.mixin.loot;

import net.minecraft.world.level.storage.loot.Serializer;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.number.NumberProvider;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(NumberProviders.class)
public interface NumberProvidersAccessor {

    @Invoker
    static LootNumberProviderType invokeRegister(String id, Serializer<? extends NumberProvider> jsonSerializer) {
        throw new AssertionError();
    }

}