package me.drex.villagerfix.util;

import com.google.common.net.UrlEscapers;
import me.drex.villagerfix.VillagerFix;
import net.fabricmc.mapping.reader.v2.MappingGetter;
import net.fabricmc.mapping.reader.v2.TinyMetadata;
import net.fabricmc.mapping.reader.v2.TinyV2Factory;
import net.fabricmc.mapping.reader.v2.TinyVisitor;
import net.minecraft.MinecraftVersion;
import org.apache.commons.io.FileUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.*;
import java.util.HashMap;
import java.util.Map;

public final class Deobfuscator {

    private static final String MAPPINGS_JAR_LOCATION = "mappings/mappings.tiny";
    private static final String NAMESPACE_FROM = "intermediary";
    private static final String NAMESPACE_TO = "named";
    private static final Path CACHED_MAPPINGS = VillagerFix.DATA_PATH
            .resolve("mappings-" +  MinecraftVersion.create().getName() + ".tiny");

    private static Map<String, String> mappings = null;

    public static void init() {
        VillagerFix.LOGGER.info("Initializing StacktraceDeobfuscator");
        try {
            if (!Files.exists(CACHED_MAPPINGS)) downloadAndCacheMappings();
        } catch (Exception e) {
            VillagerFix.LOGGER.error("Failed to load mappings!", e);
        }
        VillagerFix.LOGGER.info("Done initializing StacktraceDeobfuscator");
    }


    private static void downloadAndCacheMappings() {
        String yarnVersion;
        try {
            yarnVersion = YarnVersion.getLatestBuildForCurrentVersion();
        } catch (IOException e) {
            VillagerFix.LOGGER.error("Could not get latest yarn build for version", e);
            return;
        }

        VillagerFix.LOGGER.info("Downloading deobfuscation mappings: " + yarnVersion + " for the first launch");

        String encodedYarnVersion = UrlEscapers.urlFragmentEscaper().escape(yarnVersion);
        // Download V2 jar
        String artifactUrl = "https://maven.fabricmc.net/net/fabricmc/yarn/" + encodedYarnVersion + "/yarn-" + encodedYarnVersion + "-v2.jar";

        try {
            Files.createDirectories(VillagerFix.DATA_PATH);
        } catch (IOException e) {
            VillagerFix.LOGGER.error("Could not create data directory!", e);
            return;
        }

        File jarFile = VillagerFix.DATA_PATH.resolve("yarn-mappings.jar").toFile();
        jarFile.deleteOnExit();
        try {
            FileUtils.copyURLToFile(new URL(artifactUrl), jarFile);
        } catch (IOException e) {
            VillagerFix.LOGGER.error("Failed to downloads mappings!", e);
            return;
        }

        try (FileSystem jar = FileSystems.newFileSystem(jarFile.toPath(), (ClassLoader) null)) {
            VillagerFix.DATA_PATH.toFile().mkdirs();
            Files.copy(jar.getPath(MAPPINGS_JAR_LOCATION), CACHED_MAPPINGS, StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            VillagerFix.LOGGER.error("Failed to extract mappings!", e);
        }
    }


    private static void loadMappings() {
        if (!Files.exists(CACHED_MAPPINGS)) {
            VillagerFix.LOGGER.warn("Could not download mappings, trade factories won't be deobfuscated");
            return;
        }

        Map<String, String> mappings = new HashMap<>();

        try (BufferedReader mappingReader = Files.newBufferedReader(CACHED_MAPPINGS)) {
            TinyV2Factory.visit(mappingReader, new TinyVisitor() {
                private final Map<String, Integer> namespaceStringToColumn = new HashMap<>();

                private void addMappings(MappingGetter name) {
                    mappings.put(name.get(namespaceStringToColumn.get(NAMESPACE_FROM)).replace('/', '.'),
                            name.get(namespaceStringToColumn.get(NAMESPACE_TO)).replace('/', '.'));
                }

                @Override
                public void start(TinyMetadata metadata) {
                    namespaceStringToColumn.put(NAMESPACE_FROM, metadata.index(NAMESPACE_FROM));
                    namespaceStringToColumn.put(NAMESPACE_TO, metadata.index(NAMESPACE_TO));
                }

                @Override
                public void pushClass(MappingGetter name) {
                    addMappings(name);
                }

                @Override
                public void pushMethod(MappingGetter name, String descriptor) {
                    addMappings(name);
                }

                @Override
                public void pushField(MappingGetter name, String descriptor) {
                    addMappings(name);
                }
            });

        } catch (IOException e) {
            VillagerFix.LOGGER.error("Could not load mappings", e);
        }

        Deobfuscator.mappings = mappings;
    }

    public static String deobfuscate(String input) {
        if (mappings == null) loadMappings();
        String mapped = mappings.get(input);
        return mapped == null ? input : mapped;
    }
}
