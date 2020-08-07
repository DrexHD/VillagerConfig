package me.drex.villagerfix;

import me.drex.villagerfix.config.Config;
import me.drex.villagerfix.config.MainConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;

public class VillagerFix {

    private static final String path = System.getProperty("user.dir");
    public static final Logger LOG = LogManager.getLogger();
    public static VillagerFix INSTANCE;
    private final Config config;


    public VillagerFix() {
        INSTANCE = this;
        this.config = new Config();
        config.load();
    }

    public void reload() {
        this.config.load();
    }

    public MainConfig config() {
        return this.config.get();
    }

    public static Path configPath() {
        return new File(path + "/config").toPath();
    }

}
