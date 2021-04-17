package me.drex.villagerfix.villager.types;

import me.drex.villagerfix.util.Helper;
import net.minecraft.item.ItemStack;
import org.json.JSONObject;

public interface JsonSerializer {

    JSONObject toJson();

    String getType();

    static JSONObject parseItemStack(ItemStack itemStack) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("item", Helper.toName(itemStack.getItem()));
        jsonObject.put("amount", itemStack.getCount());
        return jsonObject;
    }

    static ItemStack parseItemStack(JSONObject jsonObject) {
        return new ItemStack(Helper.toItem(jsonObject.getString("item")), jsonObject.getInt("amount"));
    }

}
