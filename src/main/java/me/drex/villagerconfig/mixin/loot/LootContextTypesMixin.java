package me.drex.villagerconfig.mixin.loot;

import me.drex.villagerconfig.util.loot.LootContextTypes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(net.minecraft.loot.context.LootContextTypes.class)
public class LootContextTypesMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        LootContextTypes.init();
    }

}
