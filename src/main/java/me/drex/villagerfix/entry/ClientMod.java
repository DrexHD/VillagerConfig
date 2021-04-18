package me.drex.villagerfix.entry;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientLifecycleEvents;

public class ClientMod extends AbstractMod implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        load();
        ClientLifecycleEvents.CLIENT_STARTED.register(client -> {
            loadConfig();
        });
    }
}
