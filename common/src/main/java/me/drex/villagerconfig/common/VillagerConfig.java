package me.drex.villagerconfig.common;

import me.drex.villagerconfig.common.config.ConfigManager;
import me.drex.villagerconfig.common.util.TradeManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillagerConfig {

    public static final Logger LOGGER = LogManager.getLogger("VillagerConfig");
    public static final String MOD_ID = "villagerconfig";
    public static TradeManager TRADE_MANAGER;

    public static void initialize() {
        ConfigManager.load();
    }

}
