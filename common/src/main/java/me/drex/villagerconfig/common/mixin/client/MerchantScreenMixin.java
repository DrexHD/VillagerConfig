package me.drex.villagerconfig.common.mixin.client;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.villagerconfig.common.util.duck.IMerchantMenu;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.gui.screens.inventory.MerchantScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.MerchantMenu;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(MerchantScreen.class)
public abstract class MerchantScreenMixin extends AbstractContainerScreen<@NotNull MerchantMenu> {
    public MerchantScreenMixin(MerchantMenu abstractContainerMenu, Inventory inventory, Component component) {
        super(abstractContainerMenu, inventory, component);
    }

    @ModifyConstant(method = "renderProgressBar", constant = @Constant(intValue = 5, ordinal = 0))
    public int adjustMaxLevel(int constant) {
        if (((IMerchantMenu) this.menu).villagerConfig$isCustom()) {
            return ((IMerchantMenu) this.menu).villagerConfig$getMaxLevel();
        }
        return constant;
    }

    @WrapOperation(
        method = "renderProgressBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/VillagerData;getMinXpPerLevel(I)I"
        )
    )
    public int customGetMinXpPerLevel(int level, Operation<Integer> original) {
        if (((IMerchantMenu) this.menu).villagerConfig$isCustom()) {
            if (villagerconfig$canLevelUp(level)) {
                return ((IMerchantMenu) this.menu).villagerConfig$getNextLevelXpThresholds()[level - 1];
            } else {
                return 0;
            }
        }
        return original.call(level);
    }

    @WrapOperation(
        method = "renderProgressBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/VillagerData;getMaxXpPerLevel(I)I"
        )
    )
    public int customGetMaxXpPerLevel(int level, Operation<Integer> original) {
        if (((IMerchantMenu) this.menu).villagerConfig$isCustom()) {
            if (villagerconfig$canLevelUp(level)) {
                return ((IMerchantMenu) this.menu).villagerConfig$getNextLevelXpThresholds()[level];
            } else {
                return 0;
            }
        }
        return original.call(level);
    }

    @WrapOperation(
        method = "renderProgressBar",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/VillagerData;canLevelUp(I)Z"
        )
    )
    public boolean customCanLevelUp(int level, Operation<Boolean> original) {
        if (((IMerchantMenu) this.menu).villagerConfig$isCustom()) {
            return villagerconfig$canLevelUp(level);
        }
        return original.call(level);
    }


    @Unique
    private boolean villagerconfig$canLevelUp(int level) {
        return level >= 1 && level < ((IMerchantMenu) this.menu).villagerConfig$getMaxLevel();
    }
}
