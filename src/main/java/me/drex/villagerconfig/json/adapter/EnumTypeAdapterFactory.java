package me.drex.villagerconfig.json.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public final class EnumTypeAdapterFactory implements TypeAdapterFactory {
    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> typeToken) {
        Class<? super T> rawType = typeToken.getRawType();
        if (!Enum.class.isAssignableFrom(rawType) || rawType == Enum.class) {
            return null;
        }
        if (!rawType.isEnum()) {
            rawType = rawType.getSuperclass(); // handle anonymous subclasses
        }
        return (TypeAdapter<T>) new EnumAdapter(rawType);
    }
}

final class EnumAdapter<T extends Enum<T>> extends TypeAdapter<T> {
    private final Map<String, T> nameToConstant = new HashMap<>();
    private final Map<T, String> constantToName = new HashMap<>();
    private final String enumName;

    EnumAdapter(Class<T> classOfT) {
        enumName = classOfT.getName();
        for (T constant : classOfT.getEnumConstants()) {
            String name = constant.name();
            nameToConstant.put(name, constant);
            constantToName.put(constant, name);
        }
    }

    @Override
    public T read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String nextToken = in.nextString();
        T v = nameToConstant.get(nextToken);
        if (v == null) throw new IOException("Enum value " + nextToken + " is not present in " + enumName);
        return v;
    }

    @Override
    public void write(JsonWriter out, T value) throws IOException {
        out.value(value == null ? null : constantToName.get(value));
    }
}