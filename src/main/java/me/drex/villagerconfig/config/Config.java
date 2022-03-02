package me.drex.villagerconfig.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.AnnotatedSettings;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.SettingNamingConvention;
import io.github.fablabsmc.fablabs.api.fiber.v1.exception.FiberException;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.FiberSerialization;
import io.github.fablabsmc.fablabs.api.fiber.v1.serialization.JanksonValueSerializer;
import io.github.fablabsmc.fablabs.api.fiber.v1.tree.ConfigTree;
import me.drex.villagerconfig.VillagerConfig;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class Config {

    public static boolean loaded = false;
    public static final Path CONFIG_PATH = VillagerConfig.DATA_PATH.resolve("villagerconfig.json5");
    private static final AnnotatedSettings ANNOTATED_SETTINGS = AnnotatedSettings.builder()
            .useNamingConvention(SettingNamingConvention.SNAKE_CASE)
            .build();
    private static final ConfigEntries CONFIG = new ConfigEntries();
    public static final ConfigTree TREE = ConfigTree.builder()
            .applyFromPojo(CONFIG, ANNOTATED_SETTINGS)
            .build();
    private static final JanksonValueSerializer serializer = new JanksonValueSerializer(false);

    public static void load() {
        if (Files.exists(CONFIG_PATH)) {
            try {
                ANNOTATED_SETTINGS.applyToNode(TREE, CONFIG);
                FiberSerialization.deserialize(TREE, Files.newInputStream(CONFIG_PATH), serializer);
                loaded = true;
            } catch (IOException | FiberException e) {
                VillagerConfig.LOGGER.error("Failed to load config file!", e);
            }
        } else {
            saveModConfig();
            loaded = true;
        }
    }

    public static void saveModConfig() {
        try {
            ANNOTATED_SETTINGS.applyToNode(TREE, CONFIG);
            if (VillagerConfig.DATA_PATH.toFile().mkdirs()) {
                FiberSerialization.serialize(TREE, Files.newOutputStream(CONFIG_PATH), serializer);
            }
        } catch (IOException | FiberException e) {
            VillagerConfig.LOGGER.error("Failed to save config file!", e);
        }
    }

}
