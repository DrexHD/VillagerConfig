package me.drex.villagerconfig.json.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.drex.villagerconfig.mixin.RegistryKeyAccessor;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentMap;

public class TagKeyTypeAdapterFactory implements TypeAdapterFactory {

    private final DynamicRegistryManager registryManager;

    public TagKeyTypeAdapterFactory(DynamicRegistryManager registryManager) {
        this.registryManager = registryManager;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <K> TypeAdapter<K> create(Gson gson, TypeToken<K> typeToken) {
        Class<? super K> rawType = typeToken.getRawType();
        if (!TagKey.class.isAssignableFrom(rawType)) {
            return null;
        }
        return (TypeAdapter<K>) new TagKeyAdapter<>(typeToken, registryManager);
    }
}

final class TagKeyAdapter<K> extends TypeAdapter<TagKey<K>> {

    RegistryKey<Registry<K>> registryKey;

    @SuppressWarnings("unchecked")
    // Hacky solution to deserialize TagKey<K> dynamically
    public TagKeyAdapter(TypeToken<K> typeToken, DynamicRegistryManager registryManager) {
        // Retrieve all known RegistryKeys
        ConcurrentMap<RegistryKey.RegistryIdPair, RegistryKey<?>> registryKeyMap = RegistryKeyAccessor.getInstances();
        for (Map.Entry<RegistryKey.RegistryIdPair, RegistryKey<?>> entry : registryKeyMap.entrySet()) {
            RegistryKey<?> registryKey = entry.getValue();

            Optional<Registry<K>> optional = registryManager.getOptional((RegistryKey<? extends Registry<K>>) registryKey);
            if (optional.isPresent()) {
                Registry<K> registry = optional.get();
                if (isFittingRegistry(registry, getExpectedType(typeToken))) {
                    this.registryKey = (RegistryKey<Registry<K>>) registryKey;
                    return;
                }
            }
        }
        throw new IllegalArgumentException("No fitting registry found");
    }

    @Nullable
    private Type getExpectedType(TypeToken<K> typeToken) {
        Type type = typeToken.getType();
        // TagKey<T> has exactly one parameter
        if (type instanceof ParameterizedType parameterized) {
            Type[] arguments = parameterized.getActualTypeArguments();
            if (arguments.length > 0) {
                return arguments[0];
            }
        }
        return null;
    }

    private boolean isFittingRegistry(Registry<K> registry, @Nullable Type expectedArgument) {
        if (expectedArgument == null) return false;
        for (K registryEntry : registry) {
            if (expectedArgument instanceof ParameterizedType parameterized) {
                // If the type is parametrized, strip the parameters
                expectedArgument = parameterized.getRawType();
            }
            if (((Class<?>) expectedArgument).isInstance(registryEntry)) {
                return true;
            }
            break; // All entries in a registry should have the same type
        }
        return false;
    }

    @Override
    public TagKey<K> read(JsonReader in) throws IOException {
        if (in.peek() == JsonToken.NULL) {
            in.nextNull();
            return null;
        }
        String id = in.nextString();
        Identifier identifier = new Identifier(id);
        return TagKey.of(this.registryKey, identifier);
    }

    @Override
    public void write(JsonWriter out, TagKey<K> src) throws IOException {
        if (src == null) {
            out.nullValue();
            return;
        }

        Identifier identifier = src.id();
        out.value(identifier.toString());
    }

}
