package me.drex.villagerconfig.common.mixin;

import me.drex.villagerconfig.common.data.TradeTable;
import me.drex.villagerconfig.common.util.CustomVillagerData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.villager.AbstractVillager;
import net.minecraft.world.entity.npc.villager.Villager;
import net.minecraft.world.entity.npc.villager.VillagerData;
import net.minecraft.world.entity.npc.villager.VillagerTrades;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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
    public void putCustomTrades(/*? if > 1.21.10 {*/ServerLevel serverLevel, /*?}*/CallbackInfo ci) {
        TradeTable tradeTable = CustomVillagerData.getTradeTable((Villager) (Object) this);
        if (tradeTable != null) {
            VillagerData villagerData = this.getVillagerData();
            int level = villagerData./*? if >= 1.21.5 {*/ level() /*?} else {*/ /*getLevel() *//*?}*/;
            VillagerTrades.ItemListing[] tradeOffers = tradeTable.getTradeOffers(this, level);
            MerchantOffers tradeOfferList = this.getOffers();
            for (VillagerTrades.ItemListing tradeOffer : tradeOffers) {
                tradeOfferList.add(tradeOffer.getOffer(/*? if > 1.21.10 {*/serverLevel, /*?}*/this, this.random));
            }
            ci.cancel();
        }
    }

    @Redirect(
        method = "shouldIncreaseLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/VillagerData;canLevelUp(I)Z"
        )
    )
    public boolean adjustMaxLevel(int level) {
        return CustomVillagerData.canLevelUp((Villager) (Object) this, level);
    }

    @Redirect(
        method = "shouldIncreaseLevel",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/entity/npc/villager/VillagerData;getMaxXpPerLevel(I)I"
        )
    )
    public int adjustUpperLevelExperience(int level) {
        return CustomVillagerData.getMaxXpPerLevel((Villager) (Object) this, level);
    }
}
