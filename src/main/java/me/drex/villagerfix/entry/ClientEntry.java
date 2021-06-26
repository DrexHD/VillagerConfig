package me.drex.villagerfix.entry;

import me.drex.villagerfix.VillagerFix;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;

public class ClientEntry implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientTickEvents.START_WORLD_TICK.register(world -> VillagerFix.onStarted());
    }
}
