package me.drex.villagerfix.mixin;

import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.mob.HostileEntity;
import net.minecraft.entity.mob.ZombieEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.world.Difficulty;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ZombieEntity.class)
public class ZombieEntityMixin extends HostileEntity {

    protected ZombieEntityMixin(EntityType<? extends HostileEntity> entityType, World world) {
        super(entityType, world);
    }


    private Difficulty difficulty = Difficulty.PEACEFUL;

    @Inject(method = "onKilledOther", at = @At(value = "HEAD"))
    public void calculateConversionChance(ServerWorld world, LivingEntity other, CallbackInfo ci) {
        double conversionChance = ConfigEntries.features.conversionChance;
        if (conversionChance == -1) {
            difficulty = world.getDifficulty();
        } else {
            if (!Helper.chance(conversionChance)) {
                difficulty = Difficulty.EASY;
            } else {
                difficulty = Difficulty.HARD;
            }
        }
    }

    @Redirect(method = "onKilledOther", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/world/ServerWorld;getDifficulty()Lnet/minecraft/world/Difficulty;"))
    public Difficulty shouldConvert(ServerWorld world) {
        return difficulty;
    }

}
