package me.drex.villagerconfig.json.adapter;

import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;

import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.Optional;

public class RegistryTypeAdapter<K> extends TypeAdapter<K> {

    private final Registry<K> registry;

    public RegistryTypeAdapter(Registry<K> registry) {
        this.registry = registry;
    }

    @Override
    public K read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String id = in.nextString();
        Identifier identifier = new Identifier(id);
        Optional<K> optional = this.registry.getOrEmpty(identifier);
        if (optional.isEmpty()) throw new IllegalArgumentException("Unknown identifier: " + identifier);
        return optional.get();
    }

    @Override
    public void write(JsonWriter out, K src) throws IOException {
        if (src == null) {
            out.nullValue();
            return;
        }
        Identifier identifier = this.registry.getId(src);
        if (identifier != null) {
            out.value(identifier.toString());
        } else {
            throw new NoSuchElementException(src + " could not be found in " + registry.getKey().toString());
        }
    }

}
