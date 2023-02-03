package me.drex.villagerconfig.mixin.loot;

import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.loot.provider.number.LootNumberProviderTypes.class)
public abstract class LootNumberProviderTypesMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        LootNumberProviderTypes.init();
    }

}
