package me.drex.villagerconfig.fabric.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.drex.villagerconfig.common.config.ConfigScreen;
import me.drex.villagerconfig.common.platform.PlatformHooks;

public class VillagerConfigModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        if (PlatformHooks.PLATFORM_HELPER.isModLoaded("cloth-config")) {
            return ConfigScreen::getConfigScreen;
        } else {
            return ModMenuApi.super.getModConfigScreenFactory();
        }
    }

}
