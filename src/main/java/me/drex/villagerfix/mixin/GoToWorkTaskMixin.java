package me.drex.villagerfix.mixin;

import me.drex.villagerfix.VillagerFix;
import net.minecraft.entity.ai.brain.task.GoToWorkTask;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GoToWorkTask.class)
public class GoToWorkTaskMixin {

    @Inject(method = "run", at = @At(value = "RETURN"))
    private void acquireProfession(ServerWorld serverWorld, VillagerEntity villagerEntity, long l, CallbackInfo ci) {
        if (villagerEntity.getExperience() == 0 && VillagerFix.INSTANCE.config().lock) villagerEntity.setExperience(1);
    }
}
