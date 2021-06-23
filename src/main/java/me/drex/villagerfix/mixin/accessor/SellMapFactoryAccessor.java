package me.drex.villagerfix.mixin.accessor;

import net.minecraft.item.map.MapIcon;
import net.minecraft.village.TradeOffers;
import net.minecraft.world.gen.feature.StructureFeature;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(TradeOffers.SellMapFactory.class)
public interface SellMapFactoryAccessor {

    @Accessor("price")
    public int getPrice();

    @Accessor("structure")
    public StructureFeature<?> getStructure();

    @Accessor("iconType")
    public MapIcon.Type getType();

    @Accessor("maxUses")
    public int getMaxUses();

    @Accessor("experience")
    public int getExperience();


}
