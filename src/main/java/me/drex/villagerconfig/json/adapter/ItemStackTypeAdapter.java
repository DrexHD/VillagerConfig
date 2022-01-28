package me.drex.villagerconfig.json.adapter;

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
import java.util.Optional;

public class ItemStackTypeAdapter extends TypeAdapter<ItemStack> {

    public ItemStack read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        in.beginObject();
        String id = "";
        int count = 1;
        String tag = null;
        while (in.hasNext()) {
            final String name = in.nextName();
            switch (name) {
                case "id" -> id = in.nextString();
                case "count" -> count = in.nextInt();
                case "tag" -> tag = in.nextString();
                default -> throw new IllegalArgumentException("Unknown value \"" + name + "\" for ItemStack");
            }
        }
        Identifier identifier = new Identifier(id);
        final Optional<Item> optional = Registry.ITEM.getOrEmpty(identifier);
        if (optional.isEmpty()) throw new IllegalArgumentException("Unknown item: " + identifier);
        final ItemStack itemStack = new ItemStack(optional.get(), count);
        if (tag != null) {
            try {
                itemStack.setNbt(StringNbtReader.parse(tag));
            } catch (CommandSyntaxException e) {
                throw new IllegalArgumentException("Couldn't read item tag \"" + tag + "\"", e);
            }
        }
        in.endObject();
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
