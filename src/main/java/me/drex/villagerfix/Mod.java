package me.drex.villagerfix;

import me.drex.villagerfix.commands.Commands;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class Mod implements ModInitializer {

    @Override
    public void onInitialize() {
        VillagerFix.initializeData();
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            Commands.register(dispatcher);
        });
    }

}
