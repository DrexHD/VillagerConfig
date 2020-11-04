package me.drex.villagerfix.entry;

import me.drex.villagerfix.VillagerFix;
import me.drex.villagerfix.commands.Commands;
import me.drex.villagerfix.config.Config;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class AbstractMod {

    public static final Logger LOGGER = LogManager.getFormatterLogger("VillagerFix");

    public void load() {
        LOGGER.info("Initalizing VillagerFix!");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new Commands().register(dispatcher);
        });
        new VillagerFix();
    }

    public void loadConfig() {
        if (!Config.isConfigLoaded) {
            Config.load();
        }
    }
}
