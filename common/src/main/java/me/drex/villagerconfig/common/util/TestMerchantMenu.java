package me.drex.villagerconfig.common.util;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MerchantMenu;
import net.minecraft.world.item.trading.Merchant;

public class TestMerchantMenu extends MerchantMenu {
    public TestMerchantMenu(int i, Inventory inventory, Merchant merchant) {
        super(i, inventory, merchant);
    }

    @Override
    public boolean stillValid(Player player) {
        return true;
    }
}
