package me.drex.villagerconfig.util;

import net.minecraft.entity.passive.MerchantEntity;

public interface OldTradeOffer {

    void enable();

    void disable();

    void use(MerchantEntity merchant);

}
