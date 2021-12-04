package me.drex.villagerfix.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.Objects;

public class RegistryTypeAdapter<K> extends TypeAdapter<K> {

    private final Registry<K> registry;

    public RegistryTypeAdapter(Registry<K> registry) {
        this.registry = registry;
    }

    @Override
    public void write(JsonWriter out, K src) throws IOException {
        if (src == null) {
            out.nullValue();
            return;
        }
        out.value(this.registry.getId(src).toString());
    }

    @Override
    public K read(JsonReader reader) throws IOException {
        if (reader.peek() == JsonToken.NULL) {
            reader.nextNull();
            return null;
        }
        String id = reader.nextString();
        K val = this.registry.get(new Identifier(id));
        if (Objects.isNull(val)) throw new IllegalArgumentException("Unknown identifier: " + id);
        return val;
    }
}
