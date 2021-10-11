package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.map.MapIcon;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellMapFactory.class)
public interface SellMapFactoryAccessor {

    @Accessor("price")
    int getPrice();

    @Accessor("structure")
    StructureFeature<?> getStructure();

    @Accessor("iconType")
    MapIcon.Type getType();

    @Accessor("maxUses")
    int getMaxUses();

    @Accessor("experience")
    int getExperience();

}
