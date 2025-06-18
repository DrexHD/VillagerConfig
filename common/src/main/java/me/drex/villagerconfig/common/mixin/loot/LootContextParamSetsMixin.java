package me.drex.villagerconfig.common.mixin.loot;

import me.drex.villagerconfig.common.util.loot.VCLootContextParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootContextParamSets.class)
public class LootContextParamSetsMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        VCLootContextParams.init();
    }

}
