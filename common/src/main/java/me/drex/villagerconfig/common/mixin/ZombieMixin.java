package me.drex.villagerconfig.common.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import me.drex.villagerconfig.common.util.RandomUtil;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.Difficulty;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.monster/*?if > 1.21.10 {*/.zombie/*?}*/.Zombie;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.drex.villagerconfig.common.config.ConfigManager.CONFIG;

@Mixin(Zombie.class)
public abstract class ZombieMixin extends Monster {

    protected ZombieMixin(EntityType<? extends Monster> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(
        method = "killedEntity",
        at = @At("HEAD")
    )
    public void calculateConversionChance(CallbackInfoReturnable<Boolean> cir, @Share("difficulty") LocalRef<Difficulty> difficulty) {
        double conversionChance = CONFIG.features.conversionChance;
        if (conversionChance < 0D) {
            difficulty.set(this.level().getDifficulty());
        } else {
            if (!RandomUtil.chance(conversionChance)) {
                difficulty.set(Difficulty.EASY);
            } else {
                difficulty.set(Difficulty.HARD);
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
    public Difficulty shouldConvert(ServerLevel serverLevel, @Share("difficulty") LocalRef<Difficulty> difficulty) {
        return difficulty.get();
    }

}
