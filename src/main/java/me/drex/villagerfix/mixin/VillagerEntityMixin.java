package me.drex.villagerfix.mixin;

import me.drex.villagerfix.util.Helper;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @ModifyArg(method = "fillRecipes", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/passive/VillagerEntity;fillRecipesFromPool(Lnet/minecraft/village/TradeOfferList;[Lnet/minecraft/village/TradeOffers$Factory;I)V"), index = 1)
    public TradeOffers.Factory[] removeBlackListedStuff(TradeOffers.Factory[] pool) {
        return Helper.removeBlackListedItems(pool, this);
    }

}
