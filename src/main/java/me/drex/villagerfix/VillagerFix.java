package me.drex.villagerfix;

import me.drex.villagerfix.config.Config;
import me.drex.villagerfix.villager.TradeOfferParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VillagerFix {

    public static final Logger LOGGER = LogManager.getLogger();
    public static VillagerFix INSTANCE;

    public VillagerFix() {
        INSTANCE = this;
        Config.load();
    }

    public void reload() {
        Config.load();
        TradeOfferParser.cache.clear();
    }

}
