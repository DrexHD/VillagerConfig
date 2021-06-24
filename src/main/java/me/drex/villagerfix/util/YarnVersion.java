package me.drex.villagerfix.util;

import com.google.gson.Gson;
import me.drex.villagerfix.VillagerFix;
import net.minecraft.MinecraftVersion;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

public class YarnVersion {
    public int build;
    public String version;

    private static final String YARN_API_ENTRYPOINT = "https://meta.fabricmc.net/v2/versions/yarn/" +  MinecraftVersion.create().getName();
    private static final Path VERSION_FILE = VillagerFix.DATA_PATH.resolve("yarn-version.txt");
    private static String versionMemCache = null;


    public static String getLatestBuildForCurrentVersion() throws IOException {
        if (versionMemCache == null) {
            if (!Files.exists(VERSION_FILE)) {
                URL url = new URL(YARN_API_ENTRYPOINT);
                URLConnection request = url.openConnection();
                request.connect();

                YarnVersion[] versions = new Gson().fromJson(new InputStreamReader((InputStream) request.getContent()), YarnVersion[].class);
                String version = Arrays.stream(versions).max(Comparator.comparingInt(v -> v.build)).get().version;
                VillagerFix.DATA_PATH.toFile().mkdirs();
                Files.write(VERSION_FILE, version.getBytes());
                versionMemCache = version;
            } else {
                versionMemCache = new String(Files.readAllBytes(VERSION_FILE));
            }
        }

        return versionMemCache;
    }
}
