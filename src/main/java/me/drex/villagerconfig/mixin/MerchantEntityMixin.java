package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.config.ConfigEntries;
import me.drex.villagerconfig.util.IMerchantEntity;
import me.drex.villagerconfig.util.OldTradeOffer;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.MerchantScreenHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.village.Merchant;
import net.minecraft.village.TradeOffer;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import net.minecraft.world.gen.random.AbstractRandom;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity implements IMerchantEntity, Merchant {

    @Shadow
    public abstract TradeOfferList getOffers();

    @Shadow
    public abstract int getExperience();

    @Shadow
    public abstract boolean isLeveledMerchant();

    @Shadow
    public abstract @Nullable PlayerEntity getCustomer();

    // A random instance, that uses the same seed to ensure trades don't change
    private AbstractRandom semiRandom;

    protected MerchantEntityMixin(EntityType<? extends PassiveEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(
            method = "fillRecipesFromPool",
            at = @At("HEAD")
    )
    public void generateRandom(TradeOfferList recipeList, TradeOffers.Factory[] pool, int count, CallbackInfo ci) {
        if (ConfigEntries.features.tradeCycling) return;
        long seed = this.uuid.getLeastSignificantBits();
        if ((Object) this instanceof VillagerEntity villager) {
            // This is used to make sure, different levels choose different trade index
            int level = villager.getVillagerData().getLevel();
            seed += level;
        }
        semiRandom = AbstractRandom.method_43049(seed);
    }

    @Redirect(
            method = "fillRecipesFromPool",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/entity/passive/MerchantEntity;random:Lnet/minecraft/world/gen/random/AbstractRandom;"
            )
    )
    public AbstractRandom replaceRandom(MerchantEntity instance) {
        return ConfigEntries.features.tradeCycling ? instance.getRandom() : this.semiRandom;
    }

    @Redirect(
            method = "trade",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/village/TradeOffer;use()V"
            )
    )
    public void useTradeOffer(TradeOffer offer) {
        if (ConfigEntries.oldTrades.enabled) {
            ((OldTradeOffer) offer).use((MerchantEntity) (Object) this);
        } else {
            offer.use();
        }
    }

    @Override
    public void updateCustomOffers() {
        int levelProgress = 1;
        if ((Object) this instanceof VillagerEntity villagerEntity) {
            levelProgress = villagerEntity.getVillagerData().getLevel();
        }
        if (this.getCustomer() instanceof ServerPlayerEntity serverPlayerEntity) {
            if (serverPlayerEntity.currentScreenHandler instanceof MerchantScreenHandler merchantScreenHandler) {
                serverPlayerEntity.sendTradeOffers(merchantScreenHandler.syncId, this.getOffers(), levelProgress, this.getExperience(), this.isLeveledMerchant(), this.canRefreshTrades());
            }
        }
    }

    @Override
    public void enableTrades() {
        for (TradeOffer offer : this.getOffers()) {
            ((OldTradeOffer) offer).enable();
        }
        this.updateCustomOffers();
        this.addStatusEffect(new StatusEffectInstance(StatusEffects.REGENERATION, 200, 0));
    }

}
