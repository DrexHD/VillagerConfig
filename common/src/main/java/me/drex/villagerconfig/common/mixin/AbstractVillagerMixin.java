package me.drex.villagerconfig.common.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.villagerconfig.common.config.ConfigManager;
import me.drex.villagerconfig.common.util.RandomUtil;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.level.storage.loot.LootContext;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.Optional;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin {
    @WrapOperation(
        method = "addOffersFromTradeSet",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/level/storage/loot/LootContext$Builder;create(Ljava/util/Optional;)Lnet/minecraft/world/level/storage/loot/LootContext;"
        )
    )
    public LootContext addSeed(LootContext.Builder instance, Optional<Identifier> randomSequenceKey, Operation<LootContext> original) {
        if (!ConfigManager.CONFIG.features.tradeCycling) {
            instance.withOptionalRandomSeed(RandomUtil.getSeed((AbstractVillager) (Object) this));
        }
        return original.call(instance, randomSequenceKey);
    }
}
