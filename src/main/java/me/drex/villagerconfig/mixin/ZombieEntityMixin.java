package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.config.ConfigEntries;
import me.drex.villagerconfig.util.Math;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ZombieEntity.class)
public abstract class ZombieEntityMixin {

    private Difficulty difficulty = Difficulty.PEACEFUL;

    @Inject(
            method = "onKilledOther",
            at = @At("HEAD")
    )
    public void calculateConversionChance(ServerWorld world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
        double conversionChance = ConfigEntries.features.conversionChance;
        if (conversionChance < 0D) {
            difficulty = world.getDifficulty();
        } else {
            if (!Math.chance(conversionChance)) {
                difficulty = Difficulty.EASY;
            } else {
                difficulty = Difficulty.HARD;
            }
        }
    }

    @Redirect(
            method = "onKilledOther",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/world/ServerWorld;getDifficulty()Lnet/minecraft/world/Difficulty;"
            ),
            require = 0
    )
    public Difficulty shouldConvert(ServerWorld world) {
        return difficulty;
    }

}
