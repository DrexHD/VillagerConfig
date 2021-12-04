package me.drex.villagerfix.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;

public class ItemConvertibleTypeAdapter extends TypeAdapter<ItemConvertible> {

    public ItemConvertible read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String id = reader.nextString();
        return Registry.ITEM.get(new Identifier(id));
    }

    public void write(JsonWriter out, ItemConvertible src) throws IOException {
        if (src == null) {
            out.nullValue();
            return;
        }
        out.value(Registry.ITEM.getId(src.asItem()).toString());
    }

}
