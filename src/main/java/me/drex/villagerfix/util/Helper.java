package me.drex.villagerfix.util;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import me.drex.villagerfix.VillagerFix;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import org.json.JSONObject;

import java.util.Random;

public class Helper {

    public static boolean chance(double percentage) {
        return percentage >= new Random().nextDouble() * 100;
    }

    public static String toName(Item item) {
        return Registry.ITEM.getId(item).toString();
    }

    public static Item toItem(String string) {
        return Registry.ITEM.get(new Identifier(string));
    }

    public static ItemStack parseItemStack(JSONObject jsonObject) {
        ItemStack itemStack = new ItemStack(Helper.toItem(jsonObject.getString("id")), jsonObject.getInt("Count"));
        if (jsonObject.keySet().contains("tag")) {
            try {
                itemStack.setTag(StringNbtReader.parse(jsonObject.get("tag").toString()));
            } catch (CommandSyntaxException e) {
                VillagerFix.LOGGER.error("Error parsing item tag", e);
            }
        }
        return itemStack;
    }

    public static JSONObject parseItemStack(ItemStack itemStack) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("id", Helper.toName(itemStack.getItem()));
        jsonObject.put("Count", itemStack.getCount());
        if (itemStack.getTag() != null) {
            jsonObject.put("tag", new JSONObject(itemStack.getTag().toString()));
        }
        return jsonObject;
    }
}
