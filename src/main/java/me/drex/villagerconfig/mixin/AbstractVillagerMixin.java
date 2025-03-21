package me.drex.villagerconfig.mixin;

import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import me.drex.villagerconfig.util.interfaces.IVillager;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.npc.AbstractVillager;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.npc.VillagerTrades;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.trading.Merchant;
import net.minecraft.world.item.trading.MerchantOffers;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.objectweb.asm.Opcodes;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static me.drex.villagerconfig.config.ConfigManager.CONFIG;

@Mixin(AbstractVillager.class)
public abstract class AbstractVillagerMixin extends AgeableMob implements IVillager, Merchant {

    @Shadow
    public abstract @NotNull MerchantOffers getOffers();

    @Shadow
    public abstract int getVillagerXp();

    @Shadow
    public abstract boolean showProgressBar();

    @Shadow
    public abstract @Nullable Player getTradingPlayer();

    // A random instance, that uses the same seed to ensure trades don't change
    private RandomSource semiRandom;

    protected AbstractVillagerMixin(EntityType<? extends AgeableMob> entityType, Level world) {
        super(entityType, world);
    }

    @Inject(
            method = "addOffersFromItemListings",
            at = @At("HEAD")
    )
    public void generateRandom(MerchantOffers recipeList, VillagerTrades.ItemListing[] pool, int count, CallbackInfo ci) {
        if (CONFIG.features.tradeCycling) return;
        long seed = this.uuid.getLeastSignificantBits();
        if ((Object) this instanceof Villager villager) {
            // This is used to make sure, different levels choose different trade index
            int level = villager.getVillagerData().level();
            seed += level;
        }
        semiRandom = RandomSource.create(seed);
    }

    @WrapOperation(
            method = "addOffersFromItemListings",
            at = @At(
                    value = "FIELD",
                    opcode = Opcodes.GETFIELD,
                    target = "Lnet/minecraft/world/entity/npc/AbstractVillager;random:Lnet/minecraft/util/RandomSource;"
            )
    )
    public RandomSource replaceRandom(AbstractVillager merchantEntity, Operation<RandomSource> original) {
        return CONFIG.features.tradeCycling ? original.call(merchantEntity) : this.semiRandom;
    }

}
