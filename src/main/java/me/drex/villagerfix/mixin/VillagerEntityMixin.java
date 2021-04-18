package me.drex.villagerfix.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.villager.TradeOfferParser;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin extends MerchantEntity {

    public VillagerEntityMixin(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }

    @Redirect(method = "fillRecipes", at = @At(value = "INVOKE", target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"))
    public <V> V putCustomRecipes(Map map, Object key) {
        TradeOfferParser parser = TradeOfferParser.of((VillagerProfession) key);
        Int2ObjectMap<TradeOffers.Factory[]> result = parser.build();
        return (V) result;
    }

}
