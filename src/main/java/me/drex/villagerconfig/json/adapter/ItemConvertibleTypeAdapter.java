package me.drex.villagerconfig.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.Optional;

public class ItemConvertibleTypeAdapter extends TypeAdapter<ItemConvertible> {

    public ItemConvertible read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }

        String id = reader.nextString();
        Identifier identifier = new Identifier(id);
        Optional<Item> optional = Registry.ITEM.getOrEmpty(identifier);
        if (optional.isEmpty()) throw new IllegalArgumentException("Unknown identifier: " + identifier);
        return optional.get();
    }

    public void write(JsonWriter out, ItemConvertible src) throws IOException {
        if (src == null) {
            out.nullValue();
            return;
        }
        Identifier identifier = Registry.ITEM.getId(src.asItem());
        out.value(identifier.toString());
    }

}
