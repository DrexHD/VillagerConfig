package me.drex.villagerfix;

import net.fabricmc.api.ModInitializer;

public class Mod implements ModInitializer {

    @Override
    public void onInitialize() {
        VillagerFix.initializeData();
    }

}
