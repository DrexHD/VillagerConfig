package me.drex.villagerconfig.mixin;

import net.minecraft.util.registry.RegistryKey;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(RegistryKey.class)
public interface RegistryKeyAccessor {

    @Accessor("INSTANCES")
    static Map<String, RegistryKey<?>> getInstances() {
        throw new AssertionError();
    }

}
