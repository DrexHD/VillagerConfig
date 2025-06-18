package me.drex.villagerconfig.common.config;

import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting;
import io.github.fablabsmc.fablabs.api.fiber.v1.annotation.Setting.Group;

public class Config {

    @Group
    public FeaturesGroup features = new FeaturesGroup();

    public static class FeaturesGroup {
        @Setting.Constrain.Range(min = 0, max = 100)
        @Setting(comment = "The highest possible price percent discount a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        public double maxDiscount = 100;

        @Setting.Constrain.Range(min = 0, max = 100)
        @Setting(comment = "The highest possible price percent raise a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        public double maxRaise = 100;

        @Setting.Constrain.Range(min = -1, max = 100)
        @Setting(comment = "Chance for a villager to convert to a villager-zombie (-1 = vanilla behaviour, 100 = 100%)")
        public double conversionChance = -1;

        @Setting(comment = "Whether villagers trades will change, when their workstation is replaced (true = vanilla)")
        public boolean tradeCycling = true;

        @Setting(comment = "Whether villagers need to restock their trades (false = vanilla)")
        public boolean infiniteTrades = false;
    }

}
