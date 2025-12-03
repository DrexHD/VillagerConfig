package me.drex.villagerconfig.common.util.duck;

public interface IMerchantMenu {
    void villagerConfig$init(int maxLevel, int[] nextLevelXpThresholds);

    boolean villagerConfig$isCustom();
    int villagerConfig$getMaxLevel();
    int[] villagerConfig$getNextLevelXpThresholds();
}
