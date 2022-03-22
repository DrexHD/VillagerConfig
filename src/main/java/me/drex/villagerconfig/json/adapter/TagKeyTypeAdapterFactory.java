package me.drex.villagerconfig.json.adapter;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;
import me.drex.villagerconfig.mixin.RegistryKeyAccessor;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.tag.TagKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.util.registry.RegistryKey;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Optional;

public class TagKeyTypeAdapterFactory implements TypeAdapterFactory {

    @Override
    @SuppressWarnings("unchecked")
    public <K> TypeAdapter<K> create(Gson gson, TypeToken<K> typeToken) {
        Class<? super K> rawType = typeToken.getRawType();
        if (!TagKey.class.isAssignableFrom(rawType)) {
            return null;
        }
        return (TypeAdapter<K>) new TagKeyAdapter<>(typeToken);
    }
}

final class TagKeyAdapter<K> extends TypeAdapter<TagKey<K>> {

    RegistryKey<Registry<K>> registryKey;

    @SuppressWarnings("unchecked")
    // Hacky solution to deserialize TagKey<K> dynamically
    public TagKeyAdapter(TypeToken<K> typeToken) {
        // Retrieve all known RegistryKeys
        Map<String, RegistryKey<?>> registryKeyMap = RegistryKeyAccessor.getInstances();
        for (Map.Entry<String, RegistryKey<?>> entry : registryKeyMap.entrySet()) {
            RegistryKey<?> registryKey = entry.getValue();
            Optional<Registry<K>> optional = (Optional<Registry<K>>) TradeManager.getRegistryManager().getOptional((RegistryKey<? extends Registry<K>>) registryKey);
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
            TypeToken<?> registryType = TypeToken.get(registryEntry.getClass());
            String expected;
            String actual = registryType.getType().getTypeName();
            if (expectedArgument instanceof ParameterizedType parameterized) {
                // If the type is parametrized, strip the parameters
                expected = parameterized.getRawType().getTypeName();
            } else {
                expected = expectedArgument.getTypeName();
            }
            if (expected.equals(actual)) {
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
