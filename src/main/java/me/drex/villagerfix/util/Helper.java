package me.drex.villagerfix.util;

import java.util.Random;

public class Helper {

    public static boolean chance(double percentage) {
        return percentage >= Math.min(100, new Random().nextDouble()+0.01);
    }

}
