package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.VillagerConfig;
import me.drex.villagerconfig.config.ConfigEntries;
import me.drex.villagerconfig.json.behavior.TradeTable;
import me.drex.villagerconfig.util.IMerchantEntity;
import me.drex.villagerconfig.util.TradeManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Shadow
    public abstract VillagerData getVillagerData();

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "canRefreshTrades",
            at = @At("RETURN"),
            cancellable = true
    )
    public void removeRefreshTradesInfo(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigEntries.oldTrades.enabled) cir.setReturnValue(false);
    }

    @Inject(
            method = "fillRecipes",
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
            TradeOffers.Factory[] customOffers = tradeTable.getTradeOffers(level, this.random);
            this.fillRecipesFromPool(getOffers(), customOffers, customOffers.length);
            ci.cancel();
        }
    }

    @Redirect(
            method = "canLevelUp",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/VillagerData;canLevelUp(I)Z"
            )
    )
    public boolean adjustMaxLevel(int level) {
        // TODO: Client side mixin (MerchantScreen)
        return customCanLevelUp(level);
    }

    @Redirect(
            method = "canLevelUp",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/VillagerData;getUpperLevelExperience(I)I"
            )
    )
    public int adjustUpperLevelExperience(int level) {
        return customUpperLevelExperience(level);
    }

    @Inject(
            method = "levelUp",
            at = @At("TAIL")
    )
    public void onLevelUp(CallbackInfo ci) {
        if (ConfigEntries.oldTrades.enabled) ((IMerchantEntity) this).enableTrades();
    }

    private int customUpperLevelExperience(int level) {
        if (customCanLevelUp(level)) {
            TradeTable tradeTable = getTradeTable();
            if (tradeTable != null) {
                return tradeTable.requiredExperience(level + 1);
            }
        }
        return VillagerData.getUpperLevelExperience(level);
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
        if (this.world instanceof ServerWorld) {
            TradeManager tradeManager = VillagerConfig.TRADE_MANAGER;
            Identifier identifier = Registry.VILLAGER_PROFESSION.getId(this.getVillagerData().getProfession());
            return tradeManager.getTrade(identifier);
        }
        return null;
    }

}
