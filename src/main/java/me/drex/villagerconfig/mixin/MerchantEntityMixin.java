package me.drex.villagerconfig.mixin;

import me.drex.villagerconfig.config.ConfigEntries;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.PassiveEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOfferList;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.World;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(MerchantEntity.class)
public abstract class MerchantEntityMixin extends PassiveEntity {

    // A random instance, that uses the same seed to ensure trades don't change
    private Random semiRandom;

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
        semiRandom = new Random(seed);
    }

    @Redirect(
            method = "fillRecipesFromPool",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/entity/passive/MerchantEntity;random:Ljava/util/Random;"
            )
    )
    public Random replaceRandom(MerchantEntity instance) {
        return ConfigEntries.features.tradeCycling ? instance.getRandom() : this.semiRandom;
    }

}
