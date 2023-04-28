package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.json.data.TradeTable;
import me.drex.villagerconfig.util.interfaces.IVillager;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerData;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static me.drex.villagerconfig.VillagerConfig.TRADE_MANAGER;
import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

@Mixin(Villager.class)
public abstract class VillagerMixin extends AbstractVillager {

    @Shadow
    public abstract VillagerData getVillagerData();

    public VillagerMixin(EntityType<? extends AbstractVillager> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "canRestock",
            at = @At("RETURN"),
            cancellable = true
    )
    public void removeRefreshTradesInfo(CallbackInfoReturnable<Boolean> cir) {
        if (CONFIG.oldTrades.enabled) cir.setReturnValue(false);
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
            int level = villagerData.getLevel();
            VillagerTrades.ItemListing[] customOffers = tradeTable.getTradeOffers(level, this.random);
            this.addOffersFromItemListings(getOffers(), customOffers, customOffers.length);
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

    @Inject(
            method = "increaseMerchantCareer",
            at = @At("TAIL")
    )
    public void onLevelUp(CallbackInfo ci) {
        if (CONFIG.oldTrades.enabled) ((IVillager) this).enableTrades();
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
            ResourceLocation identifier = BuiltInRegistries.VILLAGER_PROFESSION.getKey(this.getVillagerData().getProfession());
            return TRADE_MANAGER.getTrade(identifier);
        }
        return null;
    }

}
