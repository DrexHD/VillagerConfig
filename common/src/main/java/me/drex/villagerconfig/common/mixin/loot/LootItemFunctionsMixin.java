package me.drex.villagerconfig.common.mixin.loot;

import me.drex.villagerconfig.common.util.loot.LootItemFunctionTypes;
//? if >= 26.3 {
import com.mojang.serialization.MapCodec;
import net.minecraft.core.Registry;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
//? }
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootItemFunctions.class)
public abstract class LootItemFunctionsMixin {

    //? if >= 26.3 {
    @Inject(method = "bootstrap", at = @At("RETURN"))
    private static void onBootstrap(Registry<MapCodec<? extends LootItemFunction>> registry, CallbackInfoReturnable<MapCodec<? extends LootItemFunction>> cir) {
        LootItemFunctionTypes.init(registry);
    }
    //? } else {
    /*
    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        LootItemFunctionTypes.init();
    }
    */
    //? }

}
