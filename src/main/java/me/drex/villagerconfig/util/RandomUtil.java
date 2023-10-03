package me.drex.villagerconfig.util;

import java.util.Random;

public class RandomUtil {

    public static boolean chance(double percentage) {
        return percentage >= new Random().nextDouble() * 100;
    }

}
