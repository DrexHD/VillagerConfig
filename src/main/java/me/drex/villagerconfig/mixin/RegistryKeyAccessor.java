package me.drex.villagerconfig.mixin;

import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;
import java.util.concurrent.ConcurrentMap;

@Mixin(RegistryKey.class)
public interface RegistryKeyAccessor {

    @Accessor("INSTANCES")
    static ConcurrentMap<RegistryKey.class_7892, RegistryKey<?>> getInstances() {
        throw new AssertionError();
    }

}
