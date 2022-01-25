package me.drex.villagerfix.mixin;

import net.minecraft.village.VillagerData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(VillagerData.class)
public interface VillagerDataAccessor {

    @Accessor("LEVEL_BASE_EXPERIENCE")
    static int[] getLevelBaseExperience() {
        throw new AssertionError();
    }
}
