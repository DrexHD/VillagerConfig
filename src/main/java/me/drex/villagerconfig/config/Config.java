package me.drex.villagerconfig.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting.Group;
import org.samo_lego.config2brigadier.IBrigadierConfigurator;
import org.samo_lego.config2brigadier.annotation.BrigadierDescription;

public class Config implements IBrigadierConfigurator {

    @Group
    public FeaturesGroup features = new FeaturesGroup();

    @Override
    public void save() {
        ConfigManager.saveModConfig();
    }

    public static class FeaturesGroup {
        @Setting.Constrain.Range(min = 0, max = 100)
        @BrigadierDescription("The highest possible price percent discount a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        @Setting(comment = "The highest possible price percent discount a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        public double maxDiscount = 100;

        @Setting.Constrain.Range(min = 0, max = 100)
        @BrigadierDescription("The highest possible price percent raise a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        @Setting(comment = "The highest possible price percent raise a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        public double maxRaise = 100;

        @Setting.Constrain.Range(min = -1, max = 100)
        @BrigadierDescription("Chance for a villager to convert to a villager-zombie (-1 = vanilla behaviour, 100 = 100%)")
        @Setting(comment = "Chance for a villager to convert to a villager-zombie (-1 = vanilla behaviour, 100 = 100%)")
        public double conversionChance = -1;

        @BrigadierDescription("Whether villagers trades will change, when their workstation is replaced (true = vanilla)")
        @Setting(comment = "Whether villagers trades will change, when their workstation is replaced (true = vanilla)")
        public boolean tradeCycling = true;

        @BrigadierDescription("Whether villagers need to restock their trades (false = vanilla)")
        @Setting(comment = "Whether villagers need to restock their trades (false = vanilla)")
        public boolean infiniteTrades = false;
    }

}
