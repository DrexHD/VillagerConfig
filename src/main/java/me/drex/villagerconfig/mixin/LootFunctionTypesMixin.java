package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.util.SetDyeFunction;
import net.minecraft.loot.function.LootFunctionType;
import net.minecraft.loot.function.LootFunctionTypes;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootFunctionTypes.class)
public abstract class LootFunctionTypesMixin {

    @Inject(method = "<clinit>", at = @At("HEAD"))
    private static void register(CallbackInfo ci) {
        VillagerConfig.SET_DYE = Registry.register(Registry.LOOT_FUNCTION_TYPE, new Identifier("set_dye"), new LootFunctionType(new SetDyeFunction.Serializer()));
    }

}
