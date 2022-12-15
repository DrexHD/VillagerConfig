package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.SetDyeFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootFunctionTypes.class)
public abstract class LootFunctionTypesMixin {

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void register(CallbackInfo ci) {
        VillagerConfig.SET_DYE = Registry.register(Registries.LOOT_FUNCTION_TYPE, new Identifier("set_dye"), new LootFunctionType(new SetDyeFunction.Serializer()));
    }

}
