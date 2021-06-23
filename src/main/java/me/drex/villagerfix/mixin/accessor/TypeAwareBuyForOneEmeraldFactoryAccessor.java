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
    public int getCount();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("experience")
    public int getExperience();

    @Accessor("map")
    public Map<VillagerType, Item> getMap();

}
