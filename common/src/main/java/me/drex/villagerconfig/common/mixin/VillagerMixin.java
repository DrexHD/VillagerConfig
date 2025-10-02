package me.drex.villagerconfig.common.mixin;

import me.drex.villagerconfig.common.data.TradeTable;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.drex.villagerconfig.common.VillagerConfig.TRADE_MANAGER;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    @Shadow
    public abstract VillagerData getVillagerData();

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
        method = "updateTrades",
        at = @At(
            value = "HEAD"
        ),
        cancellable = true
    )
    public void putCustomTrades(CallbackInfo ci) {
        TradeTable tradeTable = getTradeTable();
        if (tradeTable != null) {
            VillagerData villagerData = this.getVillagerData();
            int level = villagerData./*? if >= 1.21.5 {*/ level() /*?} else {*/ /*getLevel() *//*?}*/;
            VillagerTrades.ItemListing[] tradeOffers = tradeTable.getTradeOffers(this, level);
            MerchantOffers tradeOfferList = this.getOffers();
            for (VillagerTrades.ItemListing tradeOffer : tradeOffers) {
                tradeOfferList.add(tradeOffer.getOffer(this, this.random));
            }
            ci.cancel();
        }
    }

    @Redirect(
        method = "shouldIncreaseLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerData;canLevelUp(I)Z"
        )
    )
    public boolean adjustMaxLevel(int level) {
        // TODO: Client side mixin (MerchantScreen)
        return customCanLevelUp(level);
    }

    @Redirect(
        method = "shouldIncreaseLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/VillagerData;getMaxXpPerLevel(I)I"
        )
    )
    public int adjustUpperLevelExperience(int level) {
        return customUpperLevelExperience(level);
    }

    private int customUpperLevelExperience(int level) {
        if (customCanLevelUp(level)) {
            TradeTable tradeTable = getTradeTable();
            if (tradeTable != null) {
                return tradeTable.requiredExperience(level + 1);
            }
        }
        return VillagerData.getMaxXpPerLevel(level);
    }

    private boolean customCanLevelUp(int level) {
        TradeTable tradeTable = getTradeTable();
        if (tradeTable != null) {
            int maxLevel = tradeTable.maxLevel();
            return level >= 1 && level < maxLevel;
        }
        return VillagerData.canLevelUp(level);
    }

    private TradeTable getTradeTable() {
        if (this.level() instanceof ServerLevel) {
            ResourceLocation identifier = BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.getVillagerData()./*? if >= 1.21.5 {*/ profession().value() /*?} else {*/ /*getProfession() *//*?}*/);
            return TRADE_MANAGER.getTrade(identifier);
        }
        return null;
    }

}
