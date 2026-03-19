package me.drex.villagerconfig.common.config;

import blue.endless.jankson.Jankson;
import blue.endless.jankson.JsonElement;
import blue.endless.jankson.JsonObject;
import blue.endless.jankson.api.SyntaxError;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import me.drex.villagerconfig.common.VillagerConfig;
import me.drex.villagerconfig.common.platform.PlatformHooks;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class ConfigManager {

    public static final Path CONFIG_PATH = PlatformHooks.PLATFORM_HELPER.getModConfigDir().resolve("villagerconfig.json5");
    public static final Jankson JANKSON = Jankson.builder().build();
    public static Config CONFIG = new Config();

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                JsonObject configJson = JANKSON.load(CONFIG_PATH.toFile());
                CONFIG = JANKSON.fromJson(configJson, Config.class);
            } catch (IOException | SyntaxError e) {
                throw new RuntimeException(e);
            }
        } else {
            saveModConfig();
        }
    }

    public static void saveModConfig() {
        try {
            Files.createDirectories(CONFIG_PATH.getParent());
            String json = JANKSON.toJson(CONFIG).toJson(true, true);
            Files.writeString(CONFIG_PATH, json);
        } catch (IOException e) {
            VillagerConfig.LOGGER.error("Failed to save config file!", e);
        }
    }

}
