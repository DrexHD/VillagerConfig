package me.drex.villagerconfig.common.config;

import blue.endless.jankson.Comment;
import blue.endless.jankson.annotation.SerializedName;

public class Config {

    public FeaturesGroup features = new FeaturesGroup();

    public static class FeaturesGroup {
        @Comment("The highest possible price percent discount a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        @SerializedName("max_discount")
        public double maxDiscount = 100;

        @Comment("The highest possible price percent raise a villager can give on it's default trade price (100 = vanilla, 0 = none)")
        @SerializedName("max_raise")
        public double maxRaise = 100;

        @Comment("Chance for a villager to convert to a villager-zombie (-1 = vanilla behaviour, 100 = 100%)")
        @SerializedName("conversion_chance")
        public double conversionChance = -1;

        @Comment("Whether villagers trades will change, when their workstation is replaced (true = vanilla)")
        @SerializedName("trade_cycling")
        public boolean tradeCycling = true;

        @Comment("Whether villagers need to restock their trades (false = vanilla)")
        @SerializedName("infinite_trades")
        public boolean infiniteTrades = false;
    }

}
