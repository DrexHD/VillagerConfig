package me.drex.villagerfix.entry;

import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;

public class ServerMod extends AbstractMod implements DedicatedServerModInitializer {

    @Override
    public void onInitializeServer() {
        load();
        ServerLifecycleEvents.SERVER_STARTED.register(server -> {
            loadConfig();
        });
    }

}
