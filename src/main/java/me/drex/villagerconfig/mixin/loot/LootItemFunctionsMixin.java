package me.drex.villagerconfig.mixin.loot;

import me.drex.villagerconfig.util.loot.LootItemFunctionTypes;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctions;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LootItemFunctions.class)
public abstract class LootItemFunctionsMixin {

    @Inject(method = "<clinit>", at = @At("RETURN"))
    private static void onClInit(CallbackInfo ci) {
        LootItemFunctionTypes.init();
    }

}
