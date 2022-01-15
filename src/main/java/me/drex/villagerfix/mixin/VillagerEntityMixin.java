package me.drex.villagerfix.mixin;

import me.drex.villagerfix.config.ConfigEntries;
import me.drex.villagerfix.util.IMinecraftServer;
import me.drex.villagerfix.util.TradeManager;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Map;
import java.util.Objects;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    @Shadow
    public abstract VillagerData getVillagerData();

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(
            method = "fillRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;",
                    remap = false
            )
    )
    @SuppressWarnings("unchecked")
    public <V> V putCustomRecipes(Map<Integer, V> map, Object key) {
        TradeManager tradeManager = ((IMinecraftServer) Objects.requireNonNull(this.getServer())).getTradeManager();
        Identifier identifier = Registry.VILLAGER_PROFESSION.getId(this.getVillagerData().getProfession());
        V trade = (V) tradeManager.getTrade(identifier);
        return trade != null ? trade : map.get(key);
    }

    @Inject(
            method = "canRefreshTrades",
            at = @At("RETURN"),
            cancellable = true
    )
    public void removeRefreshTradesInfo(CallbackInfoReturnable<Boolean> cir) {
        if (ConfigEntries.oldTrades.enabled) cir.setReturnValue(false);
    }

}
