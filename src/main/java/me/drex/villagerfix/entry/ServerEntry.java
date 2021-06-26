package me.drex.villagerfix.entry;

import me.drex.villagerfix.VillagerFix;
import net.fabricmc.api.DedicatedServerModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerTickEvents;

public class ServerEntry implements DedicatedServerModInitializer {
    @Override
    public void onInitializeServer() {
        ServerTickEvents.START_WORLD_TICK.register(world -> VillagerFix.onStarted());
    }
}
