package me.drex.villagerfix.config;

import me.drex.villagerfix.OldTradeOffer;
import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

import java.util.ArrayList;

@ConfigSerializable
public class MainConfig {
    public static final String HEADER = "VillagerFix! Main Configuration File\n" +
            "Licensed Under the MIT License, Copyright (c) 2020 KiloCraft\n" +
            "VillagerFix is using HOCON for its configuration files\n learn more about it here: " +
            "https://docs.spongepowered.org/stable/en/server/getting-started/configuration/hocon.html" +
            "\nYou can use Color Codes in string parameters, the character is \"&\" " +
            "More info at: https://minecraft.tools/en/color-code.php \ne.g: \"&eThe Yellow Thing\" will be yellow";

    @Setting(comment = "The highest possible price discount a villager can give on it's default trade price (100 = vanilla, 0 = none)")
    public double maxdiscount = 100;

    @Setting(comment = "The highest possible price raise a villager can give on it's default trade price (100 = vanilla, 0 = none)")
    public double maxraise = 100;

    @Setting(comment = "Defines how high the max usages should be (100 = vanilla, 200 = 2 x more than vanilla)")
    public double maxuses = 100;

    @Setting(comment = "The chance of a villager to converting to a villagerzombie (-1 = vanilla behaviour)")
    public double conversionchance = 100;

    @Setting(comment = "Whether or not villagers should be locked to a profession once they claim it (false = vanilla)")
    public boolean lock = false;

    @Setting(comment = "Blacklist trades (this only effects newly generated trades). Example value: [\"minecraft:stick\",\"minecraft:clay\",\"minecraft:pumpkin\"]")
    public ArrayList<String> blacklisted_trades = new ArrayList<>();

    @Setting(comment = "In this config section you can configure how trade (un)locking works")
    public OldTradesSection oldtrades = new OldTradesSection();

}
