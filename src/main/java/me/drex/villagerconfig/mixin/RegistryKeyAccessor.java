package me.drex.villagerconfig.mixin;

import net.minecraft.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.concurrent.ConcurrentMap;


@Mixin(RegistryKey.class)
public interface RegistryKeyAccessor {

    @Accessor("INSTANCES")
    static ConcurrentMap<RegistryKey.RegistryIdPair, RegistryKey<?>> getInstances() {
        throw new AssertionError();
    }

}
