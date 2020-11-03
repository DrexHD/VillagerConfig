package me.drex.villagerfix;

import me.drex.villagerfix.commands.Commands;
import me.drex.villagerfix.config.Config;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Mod implements ModInitializer {

    public static final Logger LOGGER = LogManager.getFormatterLogger("VillagerFix");

    @Override
    public void onInitialize() {
        LOGGER.info("Initalizing VillagerFix!");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new Commands().register(dispatcher);
        });
        new VillagerFix();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            if (!Config.isConfigLoaded) {
                Config.load();
            }
        });
    }
}
