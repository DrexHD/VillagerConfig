package me.drex.villagerfix;

import me.drex.villagerfix.config.Config;
import me.drex.villagerfix.config.ConfigEntries;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.nio.file.Path;

public class VillagerFix {

    private static final String path = System.getProperty("user.dir");
    public static final Logger LOG = LogManager.getLogger();
    public static VillagerFix INSTANCE;


    public VillagerFix() {
        INSTANCE = this;
        Config.load();
    }

    public void reload() {
        Config.load();
    }

    public static Path configPath() {
        return new File(path + "/config").toPath();
    }

}
