package me.drex.villagerfix.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.StringNbtReader;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {

    public ItemStack read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        reader.beginObject();
        String id = "";
        int count = 1;
        String tag = null;
        while (reader.hasNext()) {
            final String name = reader.nextName();
            switch (name) {
                case "id" -> id = reader.nextString();
                case "count" -> count = reader.nextInt();
                case "tag" -> tag = reader.nextString();
                default -> throw new IllegalArgumentException("Unknown value \"" + name + "\" for ItemStack");
            }
        }
        final Item item = Registry.ITEM.get(new Identifier(id));
        final ItemStack itemStack = new ItemStack(item, count);
        if (tag != null) {
            try {
                itemStack.setNbt(StringNbtReader.parse(tag));
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Couldn't read item tag \"" + tag + "\"", e);
            }
        }
        reader.endObject();
        return itemStack;
    }

    public void write(JsonWriter out, ItemStack src) throws IOException {
        if (src == null) {
            out.nullValue();
            return;
        }
        out.beginObject();
        out.name("id");
        out.value(Registry.ITEM.getId(src.getItem()).toString());
        out.name("count");
        out.value(src.getCount());
        if (src.getNbt() != null) {
            out.name("tag");
            out.value(src.getNbt().toString());
        }
        out.endObject();
    }

}
