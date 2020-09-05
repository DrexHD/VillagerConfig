package me.drex.villagerfix.util;

import net.minecraft.item.Item;
import net.minecraft.util.registry.Registry;
import org.checkerframework.checker.nullness.qual.Nullable;

public class ItemHelper {

    public static String toName(Item item) {
        return Registry.ITEM.getId(item).toString();
    }

    @Nullable
    public static Item toItem(String string) {
        for (Item item : Registry.ITEM) {
            if (toName(item).equals(string)) return item;
        }
        return null;
    }

}
