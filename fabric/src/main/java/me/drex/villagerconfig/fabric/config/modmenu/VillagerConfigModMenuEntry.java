package me.drex.villagerconfig.fabric.config.modmenu;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.drex.villagerconfig.common.config.ConfigScreen;

public class VillagerConfigModMenuEntry implements ModMenuApi {

    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return ConfigScreen::getConfigScreen;
    }

}
