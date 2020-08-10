package me.drex.villagerfix.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class MainConfig {
    public static final String HEADER = "VillagerFix! Main Configuration File\n" +
            "Licensed Under the MIT License, Copyright (c) 2020 KiloCraft\n" +
            "VillagerFix is using HOCON for its configuration files\n learn more about it here: " +
            "https://docs.spongepowered.org/stable/en/server/getting-started/configuration/hocon.html" +
            "\nYou can use Color Codes in string parameters, the character is \"&\" " +
            "More info at: https://minecraft.tools/en/color-code.php \ne.g: \"&eThe Yellow Thing\" will be yellow";

    @Setting(comment = "The highest possible discount a villager can give on it's trades (100 = vanilla, 0 = none)")
    public double maxdiscount = 100;

    @Setting(comment = "Whether or not villagers should be locked to a profession once they claim it (false = vanilla)")
    public boolean lock = false;

}
