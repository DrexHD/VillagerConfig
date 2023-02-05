package me.drex.villagerconfig.mixin.loot;

import me.drex.villagerconfig.util.loot.LootNumberProviderTypes;
import net.minecraft.world.level.storage.loot.providers.number.NumberProviders;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(NumberProviders.class)
public abstract class NumberProvidersMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        LootNumberProviderTypes.init();
    }

}
