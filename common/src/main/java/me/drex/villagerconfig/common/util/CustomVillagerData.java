package me.drex.villagerconfig.common.util;

import me.drex.villagerconfig.common.data.TradeTable;
import me.drex.villagerconfig.common.mixin.VillagerDataAccessor;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;

import static me.drex.villagerconfig.common.VillagerConfig.TRADE_MANAGER;

public class CustomVillagerData {
    public static TradeTable getTradeTable(Villager villager) {
        if (villager.level() instanceof ServerLevel) {
            Identifier identifier = BuiltInRegistries.VILLAGER_PROFESSION.getKey(villager.getVillagerData()./*? if >= 1.21.5 {*/ profession().value() /*?} else {*/ /*getProfession() *//*?}*/);
            return TRADE_MANAGER.getTrade(identifier);
        }
        return null;
    }

    public static int getMaxLevel(Villager villager) {
        TradeTable tradeTable = getTradeTable(villager);
        if (tradeTable != null) {
            return tradeTable.maxLevel();
        }
        return VillagerData.MAX_VILLAGER_LEVEL;
    }

    public static int[] getNextLevelXpThresholds(Villager villager) {
        TradeTable tradeTable = getTradeTable(villager);
        if (tradeTable != null) {
            int[] nextLevelXpThresholds = new int[tradeTable.maxLevel()];
            for (int i = 0; i < tradeTable.maxLevel(); i++) {
                nextLevelXpThresholds[i] = tradeTable.requiredExperience(i + 1);
            }
            return nextLevelXpThresholds;
        }
        return VillagerDataAccessor.getNextLevelXpThresholds();
    }

    public static int getMinXpPerLevel(Villager villager, int level) {
        TradeTable tradeTable = getTradeTable(villager);
        if (tradeTable != null) {
            return tradeTable.requiredExperience(level);
        }
        return VillagerData.getMinXpPerLevel(level);
    }

    public static int getMaxXpPerLevel(Villager villager, int level) {
        TradeTable tradeTable = getTradeTable(villager);
        if (tradeTable != null) {
            return tradeTable.requiredExperience(level + 1);
        }
        return VillagerData.getMaxXpPerLevel(level);
    }

    public static boolean canLevelUp(Villager villager, int level) {
        TradeTable tradeTable = getTradeTable(villager);
        if (tradeTable != null) {
            int maxLevel = tradeTable.maxLevel();
            return level >= 1 && level < maxLevel;
        }
        return VillagerData.canLevelUp(level);
    }
}
