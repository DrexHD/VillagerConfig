package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.util.RandomUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {

    private Difficulty difficulty = Difficulty.PEACEFUL;

    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
            method = "killedEntity",
            at = @At("HEAD")
    )
    public void calculateConversionChance(ServerLevel serverLevel, LivingEntity livingEntity, CallbackInfoReturnable<Boolean> cir) {
        double conversionChance = CONFIG.features.conversionChance;
        if (conversionChance < 0D) {
            difficulty = this.level().getDifficulty();
        } else {
            if (!RandomUtil.chance(conversionChance)) {
                difficulty = Difficulty.EASY;
            } else {
                difficulty = Difficulty.HARD;
            }
        }
    }

    @Redirect(
            method = "killedEntity",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/server/level/ServerLevel;getDifficulty()Lnet/minecraft/world/Difficulty;"
            ),
            require = 0
    )
    public Difficulty shouldConvert(ServerLevel serverLevel) {
        return difficulty;
    }

}
