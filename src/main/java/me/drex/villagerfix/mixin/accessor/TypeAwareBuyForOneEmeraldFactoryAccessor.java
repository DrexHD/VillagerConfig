package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.Item;
import net.minecraft.village.TradeOffers;
import net.minecraft.village.VillagerType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(TradeOffers.TypeAwareBuyForOneEmeraldFactory.class)
public interface TypeAwareBuyForOneEmeraldFactoryAccessor {

    @Accessor("count")
    int getCount();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("experience")
    int getExperience();

    @Accessor("map")
    Map<VillagerType, Item> getMap();

}
