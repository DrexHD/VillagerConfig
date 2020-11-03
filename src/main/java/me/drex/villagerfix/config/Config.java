package me.drex.villagerfix.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    public static boolean isConfigLoaded = false;
    public static final Path CONFIG_PATH = FabricLoader.getInstance().getConfigDir().resolve("villagerfix.json5");
    private static final AnnotatedSettings ANNOTATED_SETTINGS = AnnotatedSettings.builder()
            .useNamingConvention(SettingNamingConvention.SNAKE_CASE)
            .build();
    private static final ConfigEntries CONFIG = new ConfigEntries();
    public static final ConfigTree TREE = ConfigTree.builder()
            .applyFromPojo(CONFIG, ANNOTATED_SETTINGS)
            .build();
    private static JanksonValueSerializer serializer = new JanksonValueSerializer(false);

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                ANNOTATED_SETTINGS.applyToNode(TREE, CONFIG);
                FiberSerialization.deserialize(TREE, Files.newInputStream(CONFIG_PATH), serializer);
                isConfigLoaded = true;
            } catch (IOException | FiberException e) {
                e.printStackTrace();
            }
        } else {
            saveModConfig();
            isConfigLoaded = true;
        }
    }

    public static void saveModConfig() {
        try {
            ANNOTATED_SETTINGS.applyToNode(TREE, CONFIG);
            FiberSerialization.serialize(TREE, Files.newOutputStream(CONFIG_PATH), serializer);
        } catch (IOException | FiberException e) {
            e.printStackTrace();
        }
    }

}
