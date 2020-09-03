package me.drex.villagerfix.config;

import ninja.leaping.configurate.objectmapping.Setting;
import ninja.leaping.configurate.objectmapping.serialize.ConfigSerializable;

@ConfigSerializable
public class OldTradesSection {

    @Setting(comment = "Whether or not https://minecraft.gamepedia.com/Trading/Before_Village_%26_Pillage#Mechanics should be used for trade (un)locking")
    public boolean enabled = false;

    @Setting(comment = "The minimum amount of trades possible it can get locked (1.12 default: 2)")
    public int minUses = 2;

    @Setting(comment = "The maximum amount of trades possible, if reached a trade is guaranteed to get locked (1.12 default: 12)")
    public int maxuses = 12;

    @Setting(comment = "The chance of a trade locking itself (1.12 default: 20)")
    public double lockchance = 20;

    @Setting(comment = "The chance of a trade unlocking all other trades (1.12 default: 20)")
    public double unlockchance = 20;

}
