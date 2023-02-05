package me.drex.villagerconfig.util.interfaces;

import net.minecraft.world.entity.npc.AbstractVillager;

public interface IMerchantOffer {

    void enable();

    void disable();

    void onUse(AbstractVillager merchant);

}
