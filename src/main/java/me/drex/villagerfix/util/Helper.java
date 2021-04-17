package me.drex.villagerfix.util;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerProfession;

import java.util.Random;

public class Helper {

    public static boolean chance(double percentage) {
        return percentage >= new Random().nextDouble() * 100;
    }

    public static String toName(Item item) {
        return Registry.ITEM.getId(item).toString();
    }

    public static Item toItem(String string) {
        for (Item item : Registry.ITEM) {
            if (toName(item).equals(string)) return item;
        }
        return null;
    }

    public static String toName(VillagerProfession profession) {
        String s = profession.toString();
        return s.substring(0, 1).toUpperCase() + s.substring(1);
    }
}
