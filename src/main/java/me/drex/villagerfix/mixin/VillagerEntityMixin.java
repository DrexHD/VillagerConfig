package me.drex.villagerfix.mixin;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import me.drex.villagerfix.villager.TradeOfferParser;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerProfession;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.util.Map;

@Mixin(VillagerEntity.class)
public abstract class VillagerEntityMixin {

    @Redirect(
            method = "fillRecipes",
            at = @At(
                    value = "INVOKE",
                    target = "Ljava/util/Map;get(Ljava/lang/Object;)Ljava/lang/Object;"
            )
    )
    public <V> V putCustomRecipes(Map map, Object key) {
        TradeOfferParser parser = TradeOfferParser.of((VillagerProfession) key, (Int2ObjectMap<TradeOffers.Factory[]>) map.get(key));
        return (V) parser.build();
    }

}
