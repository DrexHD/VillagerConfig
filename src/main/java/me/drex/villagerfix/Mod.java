package me.drex.villagerfix;

import me.drex.villagerfix.commands.Main;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;

public class Mod implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            new Main().register(dispatcher);
        });

        new VillagerFix();
    }
}
