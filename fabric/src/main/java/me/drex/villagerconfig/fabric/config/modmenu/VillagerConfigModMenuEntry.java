package me.drex.villagerconfig.fabric.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;

public class VillagerConfigModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        // TODO 26.1
//        if (PlatformHooks.PLATFORM_HELPER.isModLoaded("cloth-config")) {
//            return ConfigScreen::getConfigScreen;
//        } else {
//            return ModMenuApi.super.getModConfigScreenFactory();
//        }
        return ModMenuApi.super.getModConfigScreenFactory();
    }

}
