package me.drex.villagerconfig.common.mixin;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Villager.class)
public interface VillagerAccessor {
    @Invoker
    void invokeIncreaseMerchantCareer(/*? if > 1.21.10 {*/ServerLevel level /*?}*/);
}
