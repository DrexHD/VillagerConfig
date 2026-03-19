package me.drex.villagerconfig.common.util;

import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;

import java.util.Random;

public class RandomUtil {

    public static boolean chance(double percentage) {
        return percentage >= new Random().nextDouble() * 100;
    }

    public static long getSeed(AbstractVillager abstractVillager) {
        long seed = abstractVillager.getUUID().getLeastSignificantBits();
        if (abstractVillager instanceof Villager villager) {
            // This is used to make sure, different levels choose different trade index
            int level = villager.getVillagerData(). level() ;
            seed += level;
        }
        return seed;
    }

}
