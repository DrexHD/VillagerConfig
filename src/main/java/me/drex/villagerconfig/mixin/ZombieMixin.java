package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.Math;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Zombie;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

@Mixin(Zombie.class)
public abstract class ZombieMixin {

    private Difficulty difficulty = Difficulty.PEACEFUL;

    @Inject(
            method = "wasKilled",
            at = @At("HEAD")
    )
    public void calculateConversionChance(ServerLevel world, LivingEntity other, CallbackInfoReturnable<Boolean> cir) {
        double conversionChance = CONFIG.features.conversionChance;
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
            method = "wasKilled",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getDifficulty()Lnet/minecraft/world/Difficulty;"
            ),
            require = 0
    )
    public Difficulty shouldConvert(ServerLevel world) {
        return difficulty;
    }

}
