package me.drex.villagerconfig.common.mixin;

import me.drex.villagerconfig.common.util.duck.IMerchantMenu;
import net.minecraft.world.inventory.MerchantMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

@Mixin(MerchantMenu.class)
public class MerchantMenuMixin implements IMerchantMenu {
    @Unique
    private int maxLevel;
    @Unique
    private int[] nextLevelXpThresholds;
    @Unique
    private boolean custom = false;

    @Override
    public void villagerConfig$init(int maxLevel, int[] nextLevelXpThresholds) {
        this.maxLevel = maxLevel;
        this.nextLevelXpThresholds = nextLevelXpThresholds;
        this.custom = true;
    }

    @Override
    public boolean villagerConfig$isCustom() {
        return custom;
    }

    @Override
    public int villagerConfig$getMaxLevel() {
        return maxLevel;
    }

    @Override
    public int[] villagerConfig$getNextLevelXpThresholds() {
        return nextLevelXpThresholds;
    }
}
