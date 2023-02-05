package me.drex.villagerconfig.mixin.loot;

import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(LootContextParams.class)
public interface LootContextParamsAccessor {

    @Invoker
    static <T> LootContextParam<T> invokeCreate(String name) {
        throw new AssertionError();
    }

}
