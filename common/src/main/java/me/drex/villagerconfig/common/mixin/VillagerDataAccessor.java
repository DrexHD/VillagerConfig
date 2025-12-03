package me.drex.villagerconfig.common.mixin;

import net.minecraft.world.entity.npc.villager.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerData.class)
public interface VillagerDataAccessor {

    @Accessor("NEXT_LEVEL_XP_THRESHOLDS")
    static int[] getNextLevelXpThresholds() {
        throw new AssertionError();
    }

}
