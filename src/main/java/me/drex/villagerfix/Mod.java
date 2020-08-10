package me.drex.villagerfix;

import me.drex.villagerfix.commands.Main;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


public class Mod implements ModInitializer {

    public static final Logger LOGGER = LogManager.getFormatterLogger("VillagerFix");

    @Override
    public void onInitialize() {
        LOGGER.info("Initalizing VillagerFix!");
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new Main().register(dispatcher);
        });

        new VillagerFix();
    }
}
